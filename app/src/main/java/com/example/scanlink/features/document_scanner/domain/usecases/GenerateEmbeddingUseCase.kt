package com.example.scanlink.features.document_scanner.domain.usecases

import com.example.scanlink.features.document_scanner.domain.repositories.ISemanticSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GenerateEmbeddingUseCase @Inject constructor(
    private val repository: ISemanticSearchRepository
) {
    suspend operator fun invoke(text: String): Result<FloatArray> {
        return withContext(Dispatchers.Default) {
            repository.generateEmbedding(text)
        }
    }
}
