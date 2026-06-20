# Software Design Specification

## Authentication and Navigation Module

# 1. Overview

Module Authentication and Navigation chịu trách nhiệm:

* Quản lý vòng đời truy cập của người dùng.
* Xác định người dùng đã đăng nhập hay chưa.
* Điều hướng đến màn hình phù hợp.
* Bảo vệ các màn hình yêu cầu xác thực.
* Quản lý Session trong toàn bộ ứng dụng.

Kiến trúc được xây dựng theo:

* Single Activity Architecture.
* Jetpack Compose Navigation.
* MVVM.
* Clean Architecture.
* Unidirectional Data Flow.

---

# 2. System Architecture

```mermaid
graph TD

A[Application Start]

A-->B[MainActivity]

B-->C[AppNavigation]

C-->D[Splash Graph]

C-->E[Authentication Graph]

C-->F[Main Graph]

E-->G[Login Screen]

E-->H[Register Screen]

F-->I[Home]

F-->J[Product]

F-->K[Cart]

F-->L[Order]

F-->M[Profile]
```

MainActivity là Activity duy nhất của ứng dụng.

---

# 3. Startup Flow

Khi ứng dụng khởi động:

```mermaid
flowchart TD

A[App Start]

A-->B[MainActivity]

B-->C[AppNavigation]

C-->D[Splash Screen]

D-->E[SessionManager]

E-->F{Authenticated?}

F--No-->G[Login Screen]

F--Yes-->H[Home Screen]
```

Splash Screen chỉ có nhiệm vụ xác định trạng thái xác thực.

---

# 4. Authentication Flow

## Login Flow

```mermaid
flowchart TD

A[User]

A-->B[Login Screen]

B-->C[LoginViewModel]

C-->D[LoginUseCase]

D-->E[AuthRepository]

E-->F[Firebase Authentication]

F-->G[Spring Boot Backend]

G-->H{Success?}

H--No-->I[Show Error]

H--Yes-->J[SessionManager]

J-->K[Navigate Home]
```

---

## Register Flow

```mermaid
flowchart TD

A[User]

A-->B[Register Screen]

B-->C[RegisterViewModel]

C-->D[RegisterUseCase]

D-->E[AuthRepository]

E-->F[Firebase Create User]

F-->G[Spring Boot Create Profile]

G-->H{Success?}

H--No-->I[Show Error]

H--Yes-->J[Update Session]

J-->K[Navigate Home]
```

Có thể thay Navigate Home bằng Navigate Login tùy nghiệp vụ.

---

# 5. Session Management

SessionManager là thành phần trung tâm quản lý trạng thái đăng nhập.

```mermaid
graph TD

A[SessionManager]

A-->B[Firebase Current User]

A-->C[Stored Token]

A-->D[Authentication State]

D-->E[Authenticated]

D-->F[Unauthenticated]

D-->G[Loading]
```

Responsibilities:

* Kiểm tra Session.
* Quản lý Token.
* Logout.
* Cập nhật Authentication State.

---

# 6. Navigation Graph

```mermaid
graph TD

A[AppNavigation]

A-->B[Splash]

A-->C[Authentication]

A-->D[Main]

C-->E[Login]

C-->F[Register]

D-->G[Home]

D-->H[Products]

D-->I[Cart]

D-->J[Orders]

D-->K[Profile]

D-->L[Settings]
```

---

# 7. Login Navigation

```mermaid
flowchart TD

A[Login Screen]

A-->B[Validate Input]

B-->C[Firebase Login]

C-->D[Backend Verify]

D-->E{Success?}

E--No-->F[Show Error]

E--Yes-->G[Update Session]

G-->H[Navigate Home]

H-->I[Clear Login BackStack]
```

---

# 8. Register Navigation

```mermaid
flowchart TD

A[Register Screen]

A-->B[Validate Form]

B-->C[Firebase Register]

C-->D[Backend Create User]

D-->E{Success?}

E--No-->F[Show Error]

E--Yes-->G[Update Session]

G-->H[Navigate Home]
```

---

# 9. Logout Flow

```mermaid
flowchart TD

A[User Logout]

A-->B[Firebase SignOut]

B-->C[Clear Local Session]

C-->D[Update SessionManager]

D-->E[Navigate Login]

E-->F[Clear Main BackStack]
```

---

# 10. MVVM Interaction

```mermaid
graph LR

A[User]

A-->B[Composable Screen]

B-->C[RegisterEvent]

C-->D[ViewModel]

D-->E[UseCase]

E-->F[Repository]

F-->G[Firebase]

F-->H[Spring Boot]

G-->F

H-->F

F-->D

D-->I[RegisterState]

I-->B
```

UI chỉ gửi Event và render State.

---

# 11. Clean Architecture Layers

```mermaid
graph TD

A[Presentation]

A-->B[ViewModel]

B-->C[Domain]

C-->D[UseCase]

D-->E[Repository Interface]

E-->F[Data Layer]

F-->G[Repository Implementation]

G-->H[Firebase]

G-->I[Spring Boot API]

G-->J[DataStore]
```

Dependency chỉ đi từ ngoài vào trong.

---

# 12. Screen Responsibilities

## Splash

* Kiểm tra Session.
* Điều hướng ban đầu.

## Login

* Đăng nhập.
* Điều hướng sang Register.
* Điều hướng sang Home.

## Register

* Tạo tài khoản.
* Đồng bộ Backend.
* Điều hướng sau đăng ký.

## Home

* Màn hình chính.
* Điều hướng đến các tính năng khác.

---

# 13. Navigation Rules

Rule 1:
Người dùng chưa xác thực chỉ được truy cập Authentication Graph.

Rule 2:
Người dùng đã xác thực không quay lại Login hoặc Register bằng nút Back.

Rule 3:
Logout phải xóa toàn bộ Main Graph khỏi Back Stack.

Rule 4:
Splash chỉ xuất hiện khi khởi động ứng dụng.

---

# 14. Package Structure

```text
app/

├── MainActivity
│
├── navigation/
│   ├── AppNavigation
│   ├── AuthGraph
│   ├── MainGraph
│   └── Routes
│
├── features/
│
│   ├── splash/
│   ├── authentication/
│   │      ├── login/
│   │      └── register/
│   │
│   ├── home/
│   ├── profile/
│   ├── product/
│   ├── cart/
│   └── order/
│
├── domain/
│
├── data/
│
└── core/
```

---

# 15. Advantages

* Single Activity Architecture.
* Compose Navigation.
* MVVM.
* Clean Architecture.
* Low Coupling.
* High Cohesion.
* Scalable.
* Testable.
* Production Ready.
* Dễ mở rộng Google Login, Facebook Login, OTP và các role khác trong tương lai.
