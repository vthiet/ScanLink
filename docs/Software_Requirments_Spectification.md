# **TÀI LIỆU YÊU CẦU NGHIỆP VỤ (BRD)**

## **HỆ THỐNG QUÉT VÀ QUẢN LÝ TÀI LIỆU DI ĐỘNG (SMART SCANNER APP)**

**Phiên bản: 2.0 (Dựa trên khung chuẩn dự án EIP\_SCC)**

**Lịch sử chỉnh sửa**

| **Tác giả**      | **Ngày**              | **Lý do thay đổi**                                  | **Giai đoạn**   | **Phiên bản** |
| :--------------: | :-------------------: | :-------------------------------------------------: | :-------------: | :-----------: |
| **\[Tên bạn\]**  | **\[Ngày hiện tại\]** | **Khởi tạo tài liệu theo khung EIP\_SCC**           | **Draft**       | **1.0**       |
| **AI Assistant** | **Hôm nay**           | **Tái cấu trúc, thêm biểu đồ Mermaid và Sub-goals** | **Development** | **2.0**       |

## **1. GIỚI THIỆU (INTRODUCTION)**

### **1.1 Mục đích và Phạm vi (Purpose and Scope)**

**Tài liệu này cung cấp tập hợp các yêu cầu cốt lõi nhằm phát triển hệ thống Smart Scanner. Hệ thống bao gồm một ứng dụng di động Native (Kotlin/Java) để quét/xử lý tài liệu và một nền tảng Cloud Backend (Spring Boot) để lưu trữ, quản lý và chia sẻ tệp tin bảo mật.**

**Phạm vi tài liệu bao gồm: quản lý vòng đời tài liệu số, yêu cầu chức năng (nhóm theo Use Case), yêu cầu phi chức năng và kiến trúc phân quyền dữ liệu.**

### **1.2 Bối cảnh (Context)**

**Sự gia tăng nhu cầu số hóa giấy tờ trong kỷ nguyên số đòi hỏi một công cụ di động mạnh mẽ, có khả năng xử lý ảnh (Edge-AI) ngay trên thiết bị và đồng bộ hóa an toàn lên Cloud. Hệ thống này giúp phá vỡ rào cản chia sẻ thông tin cục bộ, cung cấp cơ chế phân quyền chuẩn mực cho các tệp PDF/Văn bản.**

### **1.3 Đối tượng độc giả (Intended Audience)**

- **Đội ngũ phát triển (Developers):** **Kỹ sư Mobile (Android Native) và Kỹ sư Backend (Spring Boot).**
- **Quản lý dự án (Project Managers):** **Theo dõi tiến độ và đối chiếu tính năng.**
- **Đội kiểm thử (QA/QC):** **Xây dựng kịch bản kiểm thử (Test cases).**

### **1.4 Cách sử dụng tài liệu (How to Use this Document)**

**Tài liệu được cấu trúc theo định hướng Mục tiêu (Goal-oriented). Người đọc nên bắt đầu từ Mục 2 để nắm bắt Mục tiêu Tổng thể, sau đó đi sâu vào từng Sub-goal, các Use Case tương ứng và bảng Đặc tả Yêu cầu kỹ thuật.**

### **1.5 Định nghĩa và Tiêu chuẩn (Definitions, Standards)**

- **OCR (Optical Character Recognition):** **Công nghệ nhận diện ký tự quang học.**
- **JWT (JSON Web Token):** **Tiêu chuẩn mã hóa chuỗi thông tin người dùng trong các phiên giao tiếp Client-Server.**
- **RBAC (Role-Based Access Control):** **Kiểm soát truy cập dựa trên vai trò.**

### **1.6 Phạm vi sản phẩm và Viễn cảnh (Product Scope and Perspective)**

**Hệ thống là một kiến trúc mở, phục vụ việc thu thập, xử lý và phân phối tài liệu số.**

### **1.7 Ngăn xếp phát triển nền tảng (The Development Stack)**

- *\[Placeholder: Chi tiết về các tầng Societal Needs, Services & Business Models, Data, Platform, Infrastructure theo cấu trúc EIP\]*

### **1.8 Lớp người dùng và Quyền truy cập (User Classes and Access)**

| **Lớp người dùng**          | **Đặc điểm và Quyền hạn**                                                                                   |
| :-------------------------: | :---------------------------------------------------------------------------------------------------------: |
| **Khách vãng lai (Guest)**  | **Sử dụng các tính năng offline của app (quét, sửa, lưu nội bộ trên máy). Không cần tài khoản.**            |
| **Người dùng (Registered)** | **Được cấp không gian lưu trữ Cloud, đồng bộ dữ liệu, tạo link Public và chia sẻ tài liệu cho người khác.** |
| **Quản trị viên (Admin)**   | **Giám sát lưu lượng, quản lý cấu hình hạn mức (Quota), phân quyền hệ thống.**                              |

### **1.9 Tài liệu người dùng (User Documentation)**

- *\[Placeholder: Danh sách các tài liệu hướng dẫn sử dụng, API Specs cho bên thứ 3 (nếu có)\]*

### **1.10 Ràng buộc thiết kế và triển khai (Design and Implementation Constraints)**

- **Mobile App:** **Bắt buộc sử dụng Kotlin/Java Native (Android SDK). Sử dụng OpenCV/CameraX cho xử lý ảnh.**
- **Backend:** **Spring Boot (v3.x+), chuẩn RESTful API, bảo mật JWT. Không sử dụng kiến trúc Monolithic quá lớn, ưu tiên Clean Architecture.**

### **1.11 Giả định (Assumptions)**

- *\[Placeholder: Các giả định về hạ tầng máy chủ mạng, chi phí duy trì Cloud, chính sách giới hạn dung lượng tải lên ban đầu, v.v.\]*

## **2. TUYÊN BỐ GIÁ TRỊ, USE CASES VÀ YÊU CẦU CHỨC NĂNG**

### **2.1 Từ Tuyên bố Giá trị đến Đặc tả Nền tảng (Value Proposition)**

**Tài liệu sử dụng mô hình hóa định hướng mục tiêu (goal-oriented). Mục tiêu chính là cung cấp nền tảng quản lý tài liệu số toàn diện.**

```mermaid
mindmap
  root((MỤC TIÊU CHÍNH: Khai thác tối đa giá trị tài liệu số))
    Sub-Goal 1: Tài liệu được số hóa thông minh
      Quét tự động
      Cân chỉnh độ méo
      Lọc màu, OCR
    Sub-Goal 2: Tài liệu được quản lý an toàn
      Lưu trữ an toàn
      Mã hóa dữ liệu
    Sub-Goal 3: Phân phối và chia sẻ linh hoạt
      Public link
      Private/Account-based share
```

### **2.2 Sơ đồ Use Case Tổng quan**

``` mermaid
flowchart LR
    User([Người dùng App])
    Guest([Khách vãng lai])

    subgraph Sub-Goal 1: Số hóa
        UC1(1. Quét và Khử méo ảnh)
        UC2(2. Áp dụng bộ lọc màu)
        UC3(3. Nhận dạng chữ OCR)
        UC4(4. Đóng gói file PDF)
    end

    subgraph Sub-Goal 2 & 3: Cloud & Share 
        UC5(5. Upload file lên Cloud)
        UC6(6. Tạo Public Link)
        UC7(7. Chia sẻ nội bộ - Viewer/Editor)
    end

    Guest --> UC1 & UC2 & UC3 & UC4
    User --> Guest
    User --> UC5 & UC6 & UC7
```

### **2.3 SUB-GOAL 1: Tài liệu được số hóa thông minh**

- **Mô tả:** **Đảm bảo quá trình đầu vào (từ camera đến bản scan PDF) dễ dàng, độ chính xác cao.**
- **Động lực (Drivers):** **Tối ưu hóa trải nghiệm người dùng cuối, giảm thiểu thao tác thủ công, tận dụng Edge-AI.**

**Bảng Yêu cầu chức năng - Nhóm Số hóa:**

| **Req. ID** | **UC ID** | **Mô tả Yêu cầu**                                                                                           | **Độ ưu tiên** | **Phạm vi (Domain)** |
| :---------: | :-------: | :---------------------------------------------------------------------------------------------------------: | :------------: | :------------------: |
| **FREQ.1**  | **UC1**   | **Tự động phát hiện viền tài liệu từ Camera stream (đạt \> 60 FPS).**                                       | **Must**       | **Mobile Client**    |
| **FREQ.2**  | **UC1**   | **Cho phép kéo 4 góc neo, áp dụng thuật toán biến đổi phối cảnh (Perspective Transform) để làm phẳng ảnh.** | **Must**       | **Mobile Client**    |
| **FREQ.3**  | **UC2**   | **Cung cấp các bộ lọc màu chuẩn: Original, Magic Color (tăng nét), Grayscale, B\&W (Thresholding).**        | **Must**       | **Mobile Client**    |
| **FREQ.4**  | **UC3**   | **Trích xuất ký tự (OCR) offline đạt độ chính xác \>95% (Tiếng Anh/Việt), hỗ trợ copy text.**               | **Should**     | **Mobile Client**    |
| **FREQ.5**  | **UC4**   | **Gom nhóm nhiều ảnh để xuất thành 1 file PDF, cho phép chọn chất lượng nén (Low/Med/High).**               | **Must**       | **Mobile Client**    |

### **2.4 SUB-GOAL 2: Tài liệu được quản lý an toàn**

- **Mô tả:** **Hệ thống tiếp nhận, lưu trữ tài liệu lên Cloud một cách toàn vẹn và được mã hóa bảo mật.**
- **Động lực:** **Bảo mật dữ liệu nhạy cảm của người dùng.**

**Bảng Yêu cầu chức năng - Nhóm Quản lý:**

| **Req. ID** | **UC ID** | **Mô tả Yêu cầu**                                                                                           | **Độ ưu tiên** | **Phạm vi (Domain)** |
| :---------: | :-------: | :---------------------------------------------------------------------------------------------------------: | :------------: | :------------------: |
| **FREQ.6**  | **UC5**   | **Backend cung cấp API Upload dạng Multipart với luồng tải lên được tối ưu hóa.**                           | **Must**       | **Spring Boot**      |
| **FREQ.7**  | **UC5**   | **Đổi tên file vật lý trên đĩa cứng thành UUID để chống Path Traversal/Dò quét trực tiếp.**                 | **Must**       | **Spring Boot**      |
| **FREQ.8**  | **UC5**   | **Đồng bộ hóa dữ liệu (Offline-first): Lưu hàng đợi ở local nếu mất mạng, tự động upload khi có Internet.** | **Should**     | **Mobile + Backend** |
| **FREQ.9**  | **Auth**  | **Cung cấp cơ chế Đăng ký, Đăng nhập sử dụng Spring Security và mã hóa BCrypt. Cấp phát JWT.**              | **Must**       | **Spring Boot**      |

### **2.5 SUB-GOAL 3: Tài liệu được phân phối / chia sẻ linh hoạt**

- **Mô tả:** **Dữ liệu có thể được truy xuất và chia sẻ tuỳ theo ý muốn của chủ sở hữu (Owner).**
- **Động lực:** **Tăng khả năng làm việc cộng tác và tương tác giữa các user.**

**Bảng Yêu cầu chức năng - Nhóm Chia sẻ:**

| **Req. ID** | **UC ID** | **Mô tả Yêu cầu**                                                                                                           | **Độ ưu tiên** | **Phạm vi (Domain)** |
| :---------: | :-------: | :-------------------------------------------------------------------------------------------------------------------------: | :------------: | :------------------: |
| **FREQ.10** | **UC6**   | **Tạo đường dẫn chia sẻ Public (Public Link) kèm Hash token bảo mật (URL không đoán được).**                                | **Must**       | **Spring Boot**      |
| **FREQ.11** | **UC6**   | **Hỗ trợ gán mật khẩu và thiết lập thời hạn hết hạn (Expiration Date) cho Public Link.**                                    | **Should**     | **Spring Boot**      |
| **FREQ.12** | **UC7**   | **Chia sẻ nội bộ theo Account (Email/Username). Cấp quyền linh hoạt: Viewer (Chỉ xem/tải) hoặc Editor (Cập nhật bản mới).** | **Must**       | **Spring Boot**      |
| **FREQ.13** | **UC7**   | **Ngăn chặn truy cập trái phép: API phải trả về 403 Forbidden nếu JWT không hợp lệ hoặc sai quyền hạn.**                    | **Must**       | **Spring Boot**      |

## **3. YÊU CẦU PHI CHỨC NĂNG (NON-FUNCTIONAL REQUIREMENTS)**

### **3.1 Yêu cầu Chất lượng Run-time (Run-time Quality)**

- **Hiệu năng & Tốc độ (Scalability/Performance):**

    - **Tác vụ xử lý ảnh (Khử méo, lọc màu) trên thiết bị di động phải hoàn tất \< 1.5 giây cho ảnh 12MP.**
    - **Độ trễ phản hồi API của Spring Boot Backend phải \< 200ms với tải thông thường.**

- **Tính khả dụng (Availability):**

    - **Uptime của hệ thống Backend yêu cầu tối thiểu 99.9%.**
    - **Giao diện người dùng trên App đảm bảo nguyên tắc Material Design 3 (luồng quét đến lưu file PDF không quá 4 bước chạm).**

- **Bảo mật & Quyền riêng tư (Security & Privacy):**

    - **Mọi truyền tải dữ liệu Client - Server đều qua HTTPS (TLS 1.3).**
    - **Mật khẩu Database không được lưu dạng plain-text. Triển khai cấu hình CORS khắt khe.**

### **3.2 Yêu cầu Phi Run-time (Non Run-time Quality)**

- **Khả năng mở rộng (Evolvability/Extensibility):** **Backend thiết kế theo kiến trúc module, hỗ trợ mở rộng tích hợp S3 Object Storage (AWS/MinIO) sau này mà không cần đập bỏ mã nguồn hiện tại (qua interface).**

### **3.3 Danh sách NFR (List of Non-Functional Requirements)**

| **ID**      | **Yêu cầu (Description)**                                                      | **Quan tâm (Concern)** | **Ưu tiên** | **Domain**  |
| :---------: | :----------------------------------------------------------------------------: | :--------------------: | :---------: | :---------: |
| **NFREQ.1** | **Giao diện hỗ trợ Dark/Light mode chuẩn Material Design 3.**                  | **Usability**          | **Should**  | **Mobile**  |
| **NFREQ.2** | **Thời gian cold start của app di động \< 2 giây.**                            | **Performance**        | **Must**    | **Mobile**  |
| **NFREQ.3** | **API hỗ trợ phân trang (pagination) và lọc để tránh quá tải payload.**        | **Scalability**        | **Must**    | **Backend** |
| **NFREQ.4** | **Tích hợp công cụ giám sát lỗi (Crashlytics) và ghi log server (ELK Stack).** | **Observability**      | **Should**  | **Cả hai**  |

## **4. YÊU CẦU KHÁC (OTHER REQUIREMENTS)**

### **4.1 Định dạng dữ liệu hỗ trợ (Supported Data Formats)**

| **Loại (MIME type)** | **Mô tả định dạng**                                    | **Extension**   | **Mức độ**       |
| :------------------: | :----------------------------------------------------: | :-------------: | :--------------: |
| **application/pdf**  | **Portable Document Format (File xuất bản cuối cùng)** | **.pdf**        | **Hỗ trợ chính** |
| **image/jpeg**       | **File ảnh gốc từ camera**                             | **.jpg, .jpeg** | **Hỗ trợ chính** |
| **text/plain**       | **Dữ liệu văn bản trích xuất từ tính năng OCR**        | **.txt**        | **Hỗ trợ**       |
| **application/json** | **Giao thức trao đổi dữ liệu REST API**                | **.json**       | **Hỗ trợ**       |

## **5. KẾT LUẬN VÀ KẾ HOẠCH TIẾP THEO (CONCLUSION & FORWARD PLANS)**

- *\[Placeholder: Chi tiết về lộ trình triển khai (Roadmap), Sprint Plan, các mốc thời gian release Beta, nghiệm thu (UAT), và đưa sản phẩm ra thị trường\]*
