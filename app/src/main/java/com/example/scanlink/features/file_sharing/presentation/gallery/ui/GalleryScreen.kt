package com.example.scanlink.features.file_sharing.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scanlink.features.file_sharing.presentation.gallery.ui.component.DocumentItem
import com.example.scanlink.features.file_sharing.presentation.viewmodel.GalleryViewModel

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel =
        hiltViewModel()
) {

    val uiState by
    viewModel.uiState.collectAsState()

    Box(
        modifier =
            Modifier.fillMaxSize()
    ) {

        when {

            uiState.isLoading -> {

                CircularProgressIndicator()
            }

            uiState.documents.isEmpty() -> {

                Text(
                    text = "Không có tài liệu",
                    modifier =
                        Modifier.padding(16.dp)
                )
            }

            else -> {

                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                ) {

                    items(
                        uiState.documents
                    ) { document ->

                        DocumentItem(
                            document = document
                        )
                    }
                }
            }
        }
    }
}