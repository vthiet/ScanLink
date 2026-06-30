package com.example.scanlink.features.document_scanner.data.engine

import android.content.Context
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.nio.LongBuffer
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ONNXEmbeddingEngine(
    private val context: Context,
    private val tokenizer: ONNXTokenizer
) {
    private var env: OrtEnvironment? = null
    private var session: OrtSession? = null
    private val mutex = Mutex()

    suspend fun loadModel() {
        mutex.withLock {
            if (env == null || session == null) {
                env = OrtEnvironment.getEnvironment()
                // bge-micro-v2 model needs to be in assets/bge-micro-v2.onnx
                val modelBytes = context.assets.open("bge-micro-v2.onnx").readBytes()
                session = env?.createSession(modelBytes, OrtSession.SessionOptions())
            }
        }
    }

    fun generateEmbedding(text: String): FloatArray {
        val currentEnv = env ?: throw IllegalStateException("ONNX environment not initialized")
        val currentSession = session ?: throw IllegalStateException("ONNX session not initialized")

        // 1. Tokenize text
        val tokens = tokenizer.tokenize(text)
        
        // Prepare inputs for the model (input_ids, attention_mask)
        val shape = longArrayOf(1, tokens.size.toLong())
        val inputIdsBuffer = LongBuffer.wrap(tokens.toLongArray())
        val attentionMaskBuffer = LongBuffer.wrap(LongArray(tokens.size) { 1L })

        val inputIdsTensor = OnnxTensor.createTensor(currentEnv, inputIdsBuffer, shape)
        val attentionMaskTensor = OnnxTensor.createTensor(currentEnv, attentionMaskBuffer, shape)

        val inputs = mapOf(
            "input_ids" to inputIdsTensor,
            "attention_mask" to attentionMaskTensor
        )

        // 2. Run inference
        currentSession.run(inputs).use { result ->
            // Assuming the output is named "sentence_embedding" or we take the first output
            val outputTensor = result[0] as OnnxTensor
            val outputArray = outputTensor.floatBuffer.array()
            
            // outputArray shape should be [1, 384] for bge-micro
            // Normalize it with L2 norm
            var sum = 0.0f
            for (v in outputArray) {
                sum += v * v
            }
            val norm = Math.sqrt(sum.toDouble()).toFloat()
            for (i in outputArray.indices) {
                outputArray[i] = outputArray[i] / norm
            }
            
            return outputArray
        }
    }

    fun close() {
        session?.close()
        env?.close()
    }
}
