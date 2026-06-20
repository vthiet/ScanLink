package com.example.scanlink.core

import com.example.scanlink.R
import java.io.IOException
import java.util.concurrent.TimeoutException

fun Throwable.toUserFriendlyErrorResId(): Int {
    return when (this) {
        // Nếu là lỗi liên quan đến đường truyền (IOException)
        is IOException -> R.string.error_unknown

        // Nếu là lỗi Timeout
        is TimeoutException -> R.string.error_unknown

        // Bạn có thể định nghĩa thêm các Exception custom của riêng mình
        // is UserNotFoundException -> R.string.error_user_not_found

        // Bắt tất cả các trường hợp còn lại
        else -> R.string.error_unknown
    }
}