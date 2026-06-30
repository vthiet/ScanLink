package com.example.scanlink.features.document_scanner.data.repositories

import com.example.scanlink.features.document_scanner.data.engine.ONNXEmbeddingEngine
import com.example.scanlink.features.document_scanner.data.local.database.dao.DocumentChunkDao
import com.example.scanlink.features.document_scanner.data.local.database.dao.DocumentDao
import com.example.scanlink.features.document_scanner.data.local.database.entities.DocumentChunkEntity
import com.example.scanlink.features.document_scanner.domain.entities.SearchResult
import com.example.scanlink.features.document_scanner.domain.repositories.ISemanticSearchRepository
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SemanticSearchRepositoryImpl @Inject constructor(
    private val chunkDao: DocumentChunkDao,
    private val documentDao: DocumentDao,
    private val engine: ONNXEmbeddingEngine
) : ISemanticSearchRepository {

    override suspend fun loadModel(): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                engine.loadModel()
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generateEmbedding(text: String): Result<FloatArray> {
        return try {
            withContext(Dispatchers.Default) {
                Result.success(engine.generateEmbedding(text))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun indexDocument(documentId: String, pageNumber: Int, text: String): Result<Unit> {
        return try {
            // Ensure model is loaded before indexing
            engine.loadModel()
            withContext(Dispatchers.IO) {
                // Chunking logic: simple implementation
                val words = text.split("\\s+".toRegex())
                val chunks = mutableListOf<String>()
                val chunkSize = 150
                val overlap = 30
                
                var start = 0
                while (start < words.size) {
                    val end = minOf(start + chunkSize, words.size)
                    val chunkText = words.subList(start, end).joinToString(" ")
                    chunks.add(chunkText)
                    if (end == words.size) break
                    start += (chunkSize - overlap)
                }

                val chunkEntities = chunks.map { chunkText ->
                    val embedding = engine.generateEmbedding(chunkText)
                    DocumentChunkEntity(
                        id = UUID.randomUUID().toString(),
                        documentId = documentId,
                        pageNumber = pageNumber,
                        rawText = chunkText,
                        embedding = embedding
                    )
                }
                
                chunkDao.insertChunks(chunkEntities)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun search(queryText: String, threshold: Float): Result<List<SearchResult>> {
        return try {
            // Ensure model is loaded before searching
            engine.loadModel()
            withContext(Dispatchers.Default) {
                val queryEmbedding = engine.generateEmbedding(queryText)
                val allChunks = chunkDao.getAllChunks()
                
                // For document titles
                val documentTitles = mutableMapOf<String, String>()

                val results = allChunks.mapNotNull { chunk ->
                    val score = dotProductSimilarity(queryEmbedding, chunk.embedding)
                    if (score >= threshold) {
                        // Fetch document title on demand if not cached
                        val docTitle = documentTitles.getOrPut(chunk.documentId) {
                            documentDao.getDocumentWithPagesById(chunk.documentId)?.document?.title ?: "Unknown Document"
                        }
                        
                        SearchResult(
                            documentId = chunk.documentId,
                            documentTitle = docTitle,
                            pageNumber = chunk.pageNumber,
                            snippet = chunk.rawText.take(100) + "...",
                            score = score
                        )
                    } else {
                        null
                    }
                }.sortedByDescending { it.score }
                
                Result.success(results)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearIndexForDocument(documentId: String): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                chunkDao.deleteChunksByDocumentId(documentId)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun dotProductSimilarity(vectorA: FloatArray, vectorB: FloatArray): Float {
        if (vectorA.size != vectorB.size) return 0.0f
        var dotProduct = 0.0f
        for (i in vectorA.indices) {
            dotProduct += vectorA[i] * vectorB[i]
        }
        return dotProduct
    }
}
