package com.example.scanlink.features.file_sharing.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.features.file_sharing.domain.usecase.GetDocumentsUseCase
import com.example.scanlink.features.file_sharing.presentation.mapper.toUiModel
import com.example.scanlink.features.file_sharing.presentation.state.GalleryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(

    private val getDocumentsUseCase:
    GetDocumentsUseCase

) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            GalleryUiState()
        )

    val uiState =
        _uiState.asStateFlow()

    init {

        loadDocuments()
    }

    fun loadDocuments() {

        viewModelScope.launch {

            _uiState.update {
                it.copy(isLoading = true)
            }

            try {

                val documents =
                    getDocumentsUseCase()
                        .map {
                            it.toUiModel()
                        }

                _uiState.update {

                    it.copy(
                        isLoading = false,
                        documents = documents
                    )
                }

            } catch (e: Exception) {

                _uiState.update {

                    it.copy(
                        isLoading = false,
                        errorMessage =
                            e.message
                    )
                }
            }
        }
    }
}