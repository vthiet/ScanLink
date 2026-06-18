package com.example.scanlink

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

    // Inject ScanEngine - Trái tim của hệ thống đã được đóng gói
    @Inject
    lateinit var scanEngine: ScanEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi tạo OpenCV
        if (OpenCVLoader.initLocal()) {
            Log.d("OpenCV", "OpenCV loaded successfully")
        } else {
            Log.e("OpenCV", "OpenCV initialization failed")
        }

        setContent {
            val context = LocalContext.current
            var scannedBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
            var detectedText by remember { mutableStateOf("Đang nhận diện chữ...") }
            var isProcessing by remember { mutableStateOf(true) }
            var pdfPath by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                try {
                    // 1. Đọc ảnh từ drawable (photo1)
                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.photo1)
                    if (bitmap == null) {
                        detectedText = "Lỗi: Không tìm thấy ảnh photo1 trong drawable"
                        isProcessing = false
                        return@LaunchedEffect
                    }

                    // 2. SỬ DỤNG SCAN ENGINE
                    // SDK này đã bao gồm: Tự động tìm khung -> Cắt ảnh -> Lọc Đen-Trắng -> OCR Tiếng Việt -> Xuất PDF
                    val result = scanEngine.fullProcess(bitmap, "ScanLink_KetQua_TiengViet")

                    // 3. Cập nhật kết quả lên UI
                    scannedBitmap = result.processedBitmap
                    detectedText = if (result.extractedText.isBlank()) "Không tìm thấy nội dung chữ." else result.extractedText
                    pdfPath = result.pdfFile?.absolutePath
                    
                    if (result.isDocumentDetected) {
                        Log.d("SCAN_SUCCESS", "Đã tìm thấy tài liệu và cắt ảnh thành công")
                    } else {
                        Log.d("SCAN_WARNING", "Không tìm thấy khung giấy, đang xử lý trên toàn bộ ảnh")
                    }

                } catch (e: Exception) {
                    detectedText = "Lỗi xử lý: ${e.message}"
                    Log.e("SCAN_ERROR", e.stackTraceToString())
                } finally {
                    isProcessing = false
                }
            }

            // Giao diện hiển thị (Compose UI)
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ScanLink - Tiếng Việt OCR",
                            fontSize = 22.sp,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Hiển thị ảnh sau khi đã qua bộ lọc (B&W)
                        scannedBitmap?.let {
                            Card(
                                modifier = Modifier.fillMaxWidth().height(400.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Scanned Image",
                                    modifier = Modifier.fillMaxSize().background(Color.Black)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Nút thông báo PDF
                        pdfPath?.let { path ->
                            Button(
                                onClick = { Toast.makeText(context, "File lưu tại: $path", Toast.LENGTH_LONG).show() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                            ) {
                                Text("📁 Đã xuất PDF (Bấm để xem đường dẫn)", color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Vùng hiển thị nội dung chữ nhận diện được
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "VĂN BẢN TRÍCH XUẤT (TIẾNG VIỆT):",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.Gray
                                )
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Text(
                                    text = detectedText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    lineHeight = 24.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
