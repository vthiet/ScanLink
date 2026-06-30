package com.example.scanlink.features.document_scanner.data.engine

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class ONNXTokenizer(private val context: Context) {
    private var vocab: Map<String, Long>? = null

    @Synchronized
    private fun getVocab(): Map<String, Long> {
        if (vocab == null) {
            val vocabMap = mutableMapOf<String, Long>()
            try {
                context.assets.open("vocab.txt").use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
                        lines.forEachIndexed { index, line ->
                            vocabMap[line.trim()] = index.toLong()
                        }
                    }
                }
            } catch (e: Exception) {
                // If vocab.txt is missing, we leave it empty to trigger the fallback
                e.printStackTrace()
            }
            vocab = vocabMap
        }
        return vocab!!
    }

    fun tokenize(text: String): List<Long> {
        val vocab = getVocab()
        if (vocab.isEmpty()) {
            // Fallback stub: if vocab.txt does not exist
            return fallbackTokenize(text)
        }

        val tokens = mutableListOf<Long>()
        tokens.add(vocab["[CLS]"] ?: 101L) // [CLS] token

        val cleanText = preProcess(text.lowercase())
        val words = cleanText.split(" ")

        for (word in words) {
            if (word.isEmpty()) continue
            
            var start = 0
            val len = word.length
            val wordTokens = mutableListOf<Long>()
            var isBad = false

            while (start < len) {
                var end = len
                var curSubword: String? = null
                while (start < end) {
                    var substr = word.substring(start, end)
                    if (start > 0) {
                        substr = "##" + substr
                    }
                    if (vocab.containsKey(substr)) {
                        curSubword = substr
                        break
                    }
                    end--
                }
                if (curSubword == null) {
                    isBad = true
                    break
                }
                wordTokens.add(vocab[curSubword]!!)
                start = end
            }

            if (isBad) {
                tokens.add(vocab["[UNK]"] ?: 100L) // [UNK] token
            } else {
                tokens.addAll(wordTokens)
            }
        }

        tokens.add(vocab["[SEP]"] ?: 102L) // [SEP] token
        return tokens
    }

    private fun preProcess(text: String): String {
        val sb = StringBuilder()
        for (char in text) {
            if (isPunctuation(char)) {
                sb.append(" ").append(char).append(" ")
            } else {
                sb.append(char)
            }
        }
        return sb.toString().trim().replace("\\s+".toRegex(), " ")
    }

    private fun isPunctuation(char: Char): Boolean {
        val type = Character.getType(char).toByte()
        return type == Character.CONNECTOR_PUNCTUATION ||
                type == Character.DASH_PUNCTUATION ||
                type == Character.START_PUNCTUATION ||
                type == Character.END_PUNCTUATION ||
                type == Character.INITIAL_QUOTE_PUNCTUATION ||
                type == Character.FINAL_QUOTE_PUNCTUATION ||
                type == Character.OTHER_PUNCTUATION ||
                (char in "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~")
    }

    private fun fallbackTokenize(text: String): List<Long> {
        val words = text.lowercase().split("\\s+".toRegex())
        val tokens = mutableListOf<Long>()
        tokens.add(101L) // CLS
        for (w in words) {
            tokens.add(w.hashCode().toLong().coerceIn(1000L, 30000L))
        }
        tokens.add(102L) // SEP
        return tokens
    }
}

