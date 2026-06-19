package com.example.scanlink

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scanlink.features.document_scanner.data.engine.ScanEngine
import dagger.hilt.android.AndroidEntryPoint
import org.opencv.android.OpenCVLoader
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var scanEngine: ScanEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (OpenCVLoader.initLocal()) {
            Log.d("OpenCV", "OpenCV loaded successfully")
        }

        setContent {
            val context = LocalContext.current
            var scannedBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
            var detectedText by remember { mutableStateOf("Đang nhận diện chữ...") }
            var isProcessing by remember { mutableStateOf(true) }
            var pdfPath by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                try {
                    // FIX: Đọc ảnh không bị scale bởi hệ thống để giữ độ nét cao nhất cho OCR
                    val options = BitmapFactory.Options().apply {
                        inScaled = false
                        inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888
                    }
                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.photo5, options)
                    
                    if (bitmap == null) {
                        detectedText = "Lỗi: Không tìm thấy ảnh photo1"
                        isProcessing = false
                        return@LaunchedEffect
                    }

                    val result = scanEngine.fullProcess(bitmap, "_Scan_Result")

                    scannedBitmap = result.processedBitmap
                    detectedText = result.extractedText.ifBlank { "Không tìm thấy nội dung." }
                    pdfPath = result.pdfFile?.absolutePath

                } catch (e: Exception) {
                    detectedText = "Lỗi xử lý: ${e.message}"
                    Log.e("SCAN_ERROR", e.stackTraceToString())
                } finally {
                    isProcessing = false
                }
            }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("ScanLink - OCR Fix", fontSize = 22.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))

                        if (isProcessing) CircularProgressIndicator()

                        scannedBitmap?.let {
                            Card(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().background(Color.Black)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("VĂN BẢN TRÍCH XUẤT:", style = MaterialTheme.typography.labelLarge)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Text(detectedText, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
