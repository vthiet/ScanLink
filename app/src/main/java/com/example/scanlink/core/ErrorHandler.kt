package com.example.scanlink.core

import com.example.scanlink.R
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import java.io.IOException
import java.util.concurrent.TimeoutException

fun Throwable.toUserFriendlyErrorResId(): Int {
    return when (this) {
        // Sai mật khẩu hoặc email không hợp lệ
        is FirebaseAuthInvalidCredentialsException -> R.string.error_wrong_password

        // Tài khoản không tồn tại hoặc đã bị vô hiệu hóa
        is FirebaseAuthInvalidUserException -> R.string.error_user_not_found

        // Nếu là lỗi liên quan đến đường truyền (IOException)
        is IOException -> R.string.error_unknown

        // Nếu là lỗi Timeout
        is TimeoutException -> R.string.error_unknown

        // Bắt tất cả các trường hợp còn lại
        else -> {
            // Kiểm tra message để xử lý "too many requests"
            val msg = message?.lowercase() ?: ""
            if (msg.contains("too many")) {
                R.string.error_too_many_requests
            } else {
                R.string.error_unknown
            }
        }
    }
}