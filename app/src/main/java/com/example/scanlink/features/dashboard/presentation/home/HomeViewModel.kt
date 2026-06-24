package com.example.scanlink.features.dashboard.presentation.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.features.document_scanner.domain.usecases.CreateMultiImagePdfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val createMultiImagePdfUseCase: CreateMultiImagePdfUseCase
) : ViewModel() {

    fun createPdfFromUris(context: Context, uris: List<Uri>) {
        if (uris.isEmpty()) return

        viewModelScope.launch {
            try {
                // 1. Chuyển đổi URIs thành Bitmaps (chạy trên IO thread)
                val bitmaps = withContext(Dispatchers.IO) {
                    uris.mapNotNull { uri ->
                        context.contentResolver.openInputStream(uri)?.use { 
                            BitmapFactory.decodeStream(it) 
                        }
                    }
                }

                if (bitmaps.isNotEmpty()) {
                    // 2. Gọi Use Case để tạo PDF
                    val pdfFile = createMultiImagePdfUseCase(bitmaps, "ScanLink_Imported_${System.currentTimeMillis()}")
                    
                    if (pdfFile != null) {
                        Toast.makeText(context, "Đã tạo PDF thành công tại: ${pdfFile.name}", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Lỗi khi tạo PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
