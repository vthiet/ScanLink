package com.example.scanlink.features.document_scanner.domain.usecases

import com.example.scanlink.features.document_scanner.domain.entities.SearchResult
import com.example.scanlink.features.document_scanner.domain.repositories.ISemanticSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchSemanticChunksUseCase @Inject constructor(
    private val repository: ISemanticSearchRepository
) {
    suspend operator fun invoke(queryText: String, threshold: Float = 0.6f): Result<List<SearchResult>> {
        return withContext(Dispatchers.Default) {
            repository.search(queryText, threshold)
        }
    }
}
