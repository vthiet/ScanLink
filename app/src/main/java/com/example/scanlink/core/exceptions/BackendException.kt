package com.example.scanlink.core.exceptions

/** HTTP 400 – Tham số đầu vào không hợp lệ hoặc sai định dạng */
class BadRequestException(message: String = "Bad request") : Exception(message)

/** HTTP 401 – Token Firebase không hợp lệ hoặc đã hết hạn (xác thực phía backend) */
class BackendUnauthorizedException(message: String = "Unauthorized") : Exception(message)

/** HTTP 403 – Người dùng không có quyền thao tác trên tài nguyên */
class ForbiddenException(message: String = "Forbidden") : Exception(message)

/**
 * HTTP 404 – Tài khoản đã đăng nhập Firebase nhưng chưa được đồng bộ
 * sang cơ sở dữ liệu backend (SDD INT-API-002).
 */
class AccountNotSyncedException(message: String = "Account not synced") : Exception(message)

/** HTTP 409 – Email đã được đăng ký trên hệ thống backend */
class EmailAlreadyExistsException(message: String = "Email already exists") : Exception(message)

/** HTTP 413 – Kích thước payload/file vượt quá giới hạn cho phép (10MB) */
class PayloadTooLargeException(message: String = "Payload too large") : Exception(message)

/** HTTP 500+ – Lỗi nội bộ server */
class ServerErrorException(message: String = "Internal server error") : Exception(message)

/** Server trả về data null/rỗng dù status 2xx */
class InvalidServerResponseException(message: String = "Invalid server response") : Exception(message)
