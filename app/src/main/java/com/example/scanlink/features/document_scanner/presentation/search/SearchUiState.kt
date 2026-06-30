package com.example.scanlink.features.document_scanner.presentation.search

import com.example.scanlink.features.document_scanner.domain.entities.SearchResult

data class SearchUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val results: List<SearchResult> = emptyList(),
    val error: String? = null,
    val isModelLoaded: Boolean = false,
    val isFallbackMode: Boolean = false
)
