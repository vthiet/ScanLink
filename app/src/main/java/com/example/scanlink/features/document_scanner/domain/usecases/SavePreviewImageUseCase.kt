package com.example.scanlink.features.document_scanner.domain.usecases

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.scanlink.features.document_scanner.domain.repositories.IPreviewImageRepository
import javax.inject.Inject

class SavePreviewImageUseCase @Inject constructor(
    private val previewImageRepository: IPreviewImageRepository
) {
    operator fun invoke(context: Context, bitmap: Bitmap, fileName: String): Uri {
        return previewImageRepository.saveToPictures(context, bitmap, fileName)
    }
}
