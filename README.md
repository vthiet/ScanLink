# ScanLink - Hệ thống Quét và Quản lý Tài liệu Di động Thông minh

ScanLink là giải pháp số hóa và quản lý tài liệu thông minh trên thiết bị di động Android. Ứng dụng tích hợp các mô hình học máy (Machine Learning) để nhận diện văn bản (OCR) và cung cấp công cụ **Tìm kiếm Ngữ nghĩa Offline (Offline Semantic Search)** hoàn toàn độc lập, bảo mật và an toàn cho dữ liệu người dùng.

---

## 🚀 Các Tính Năng Chính (Core Features)

1. **Quét tài liệu thông minh (Smart Scan & Crop):**
   - Tự động nhận diện góc cạnh tài liệu thông qua OpenCV.
   - Hiệu chỉnh phối cảnh (Perspective Warp) và căn góc phẳng.
   - Áp dụng các bộ lọc tối ưu hóa văn bản: Màu gốc (Original), Đen trắng (Black & White), Xám (Grayscale), và Phục hồi màu sắc (Magic Color).

2. **Nhận diện văn bản offline (Google ML Kit OCR):**
   - Trích xuất chữ viết thô từ hình ảnh tài liệu quét nhanh chóng và chính xác.
   - Lưu trữ văn bản trang cục bộ phục vụ chế độ offline và đồng bộ lên server.

3. **Tìm kiếm Ngữ nghĩa Offline (Offline Semantic Search):**
   - Tự động phân tách nhỏ văn bản (Chunking) của tài liệu thành các đoạn liên kết.
   - Sử dụng mô hình mạng neural cục bộ `bge-micro-v2` nạp qua **ONNX Runtime Mobile** để chuyển đổi đoạn văn thành vector nhúng **384 chiều**.
   - Thực hiện tìm kiếm câu hỏi ngôn ngữ tự nhiên thông qua thuật toán so khớp **Cosine Similarity (Dot Product)** trực tiếp trên bộ nhớ đệm (RAM) của thiết bị với thời gian phản hồi < 10ms.

4. **Đồng bộ và Lưu trữ đám mây (Cloud Sync & Backup):**
   - Sao lưu dữ liệu an toàn lên đám mây (AWS S3) thông qua Spring Boot Backend.
   - Lưu trữ cơ sở dữ liệu cục bộ bằng SQLite thông qua Jetpack Room Database hỗ trợ CASCADE DELETE đồng bộ dữ liệu.

5. **Chia sẻ và Cộng tác bảo mật (Secure Sharing):**
   - Tạo liên kết chia sẻ công khai (Public Link) kèm tùy chọn mật khẩu bảo vệ và thời gian hết hạn link.
   - Cấp quyền cộng tác nội bộ (Private Permission - Viewer/Editor) dựa trên email tài khoản của người nhận.

---

## 🛠️ Công Nghệ Sử Dụng (Technology Stack)

### **Android Client (Mobile Application)**
*   **Ngôn ngữ:** Kotlin
*   **Giao diện:** Jetpack Compose (Khai báo giao diện hiện đại, mượt mà)
*   **Cơ sở dữ liệu:** Room DB (SQLite wrapper của Android Jetpack)
*   **Dependency Injection:** Dagger Hilt
*   **Mạng & API Client:** Retrofit 2 + Gson Converter
*   **Xử lý bất đồng bộ:** Kotlin Coroutines & Flow
*   **Học máy cục bộ:**
    - Google ML Kit Text Recognition (OCR)
    - ONNX Runtime Android (Inference mô hình nhúng `bge-micro-v2.onnx`)

### **Spring Boot Backend (Server)**
*   **Ngôn ngữ & Framework:** Java 25 (LTS) & Spring Boot 3.5+
*   **Mạng đồng thì:** Sử dụng Virtual Threads (Project Loom) nâng cao hiệu năng xử lý.
*   **Cơ sở dữ liệu:** MongoDB (lưu trữ metadata linh hoạt dạng document).
*   **Xác thực:** Firebase Admin SDK phối hợp Spring Security (JWT Token).
*   **Lưu trữ file:** S3-compatible cloud storage.

---

## 📐 Kiến Trúc Dự Án (Architecture)

### **Android Client (Clean Architecture & Feature-First)**
Mã nguồn được phân tách theo tính năng (Feature-First) nhằm tăng tính modular và dễ bảo trì:
```
app/src/main/java/com/example/scanlink/
├── core/                        # Cấu hình dùng chung (DI, Network, UI Components)
├── navigation/                  # Quản lý đồ thị màn hình và điều hướng AppNavGraph
└── features/
    ├── dashboard/               # Giao diện Trang chủ và Hồ sơ người dùng
    ├── file_sharing/            # Luồng tải tài liệu, chia sẻ link và xem lịch sử
    └── document_scanner/        # Mô-đun cốt lõi số hóa tài liệu
        ├── data/                # Database Room, ONNX Engine, OpenCV, và Repository Impl
        ├── domain/              # Entities nghiệp vụ (Document, Page) và Use Cases
        └── presentation/        # Giao diện Camera, Preview, OCR, và SearchScreen
```

---

## 🛠️ Hướng Dẫn Phát Triển & Chạy Dự Án (Setup & Execution)

### **Yêu cầu hệ thống (Prerequisites)**
*   JDK 21 hoặc JDK 25 (Cho phía Backend)
*   Android SDK (API Level 26+)
*   Android Studio Ladybug trở lên

### **Chạy Unit Test trên thiết bị local**
Dự án đã định cấu hình kiểm thử tự động toàn diện kiểm chứng luồng lưu trữ và đánh chỉ mục vector tìm kiếm. Để chạy các test này, thực thi lệnh trong terminal:
```bash
./gradlew testDebugUnitTest
```

### **Biên dịch và đóng gói ứng dụng (Build)**
Để biên dịch mã nguồn Kotlin và xác thực tính đúng đắn trước khi chạy:
```bash
./gradlew compileDebugKotlin
```

---

## 📂 Tài Liệu Kỹ Thuật Đọc Thêm (Documentation)

Chi tiết cấu trúc thiết kế chi tiết (IEEE Compliant) được lưu trữ tại thư mục `/docs`:
- [Software_Design_Description.md](file:///mnt/Data/dev/java/ScanLink/docs/Software_Design_Description.md): Đặc tả thiết kế chi tiết toàn bộ hệ thống (Android & Spring Boot Backend).
- [Document_Semantic_Search_Specification.md](file:///mnt/Data/dev/java/ScanLink/docs/Document_Semantic_Search_Specification.md): Đặc tả chi tiết thiết kế thuật toán Tìm kiếm Ngữ nghĩa Offline.
- [Google_Sign_In_Feature_Specification.md](file:///mnt/Data/dev/java/ScanLink/docs/Google_Sign_In_Feature_Specification.md): Đặc tả luồng xác thực Google Sign-in cục bộ và đồng bộ máy chủ.
- [Software_Requirments_Spectification.md](file:///mnt/Data/dev/java/ScanLink/docs/Software_Requirments_Spectification.md): Tài liệu đặc tả yêu cầu phần mềm (SRS).
