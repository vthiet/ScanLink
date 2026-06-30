package com.example.scanlink.features.document_scanner.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.features.document_scanner.domain.repositories.ISemanticSearchRepository
import com.example.scanlink.features.document_scanner.domain.usecases.SearchSemanticChunksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchSemanticChunksUseCase,
    private val repository: ISemanticSearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadModel()
    }

    private fun loadModel() {
        viewModelScope.launch {
            repository.loadModel()
                .onSuccess {
                    _uiState.update { it.copy(isModelLoaded = true, error = null) }
                }
                .onFailure {
                    _uiState.update { 
                        it.copy(
                            isModelLoaded = false, 
                            isFallbackMode = true,
                            error = "Không thể tải mô hình AI. Đang sử dụng chế độ tìm kiếm cơ bản."
                        ) 
                    }
                }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun search() {
        val query = _uiState.value.query
        if (query.isBlank()) return

        _uiState.update { it.copy(isSearching = true, error = null) }

        viewModelScope.launch {
            if (_uiState.value.isModelLoaded) {
                searchUseCase(query)
                    .onSuccess { results ->
                        _uiState.update { it.copy(results = results, isSearching = false) }
                    }
                    .onFailure { e ->
                        _uiState.update { 
                            it.copy(
                                isSearching = false, 
                                error = e.message ?: "Đã xảy ra lỗi trong quá trình tìm kiếm."
                            ) 
                        }
                    }
            } else {
                // Fallback basic search logic (stub)
                _uiState.update { 
                    it.copy(
                        isSearching = false, 
                        results = emptyList() // Implement basic search later
                    ) 
                }
            }
        }
    }
}
