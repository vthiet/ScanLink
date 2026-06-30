package com.example.scanlink.core

import com.example.scanlink.R
import com.example.scanlink.core.exceptions.AccountNotSyncedException
import com.example.scanlink.core.exceptions.BackendUnauthorizedException
import com.example.scanlink.core.exceptions.BadRequestException
import com.example.scanlink.core.exceptions.EmailAlreadyExistsException
import com.example.scanlink.core.exceptions.ForbiddenException
import com.example.scanlink.core.exceptions.InvalidServerResponseException
import com.example.scanlink.core.exceptions.PayloadTooLargeException
import com.example.scanlink.core.exceptions.ServerErrorException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

fun Throwable.toUserFriendlyErrorResId(): Int {
    return when (this) {

        // ── Lỗi Firebase Authentication (phía client) ────────────────────────
        is FirebaseAuthInvalidCredentialsException -> R.string.error_wrong_password
        is FirebaseAuthInvalidUserException        -> R.string.error_user_not_found
        is FirebaseAuthUserCollisionException      -> R.string.error_email_already_exists
        
        // ── Lỗi Google Sign-In ────────────────────────────────────────────────
        is com.example.scanlink.core.exceptions.GoogleEmailCollisionException -> R.string.error_email_collision_google
        is com.example.scanlink.core.exceptions.GoogleSignInUnavailableException -> R.string.error_google_sign_in_unavailable
        is androidx.credentials.exceptions.GetCredentialCancellationException -> R.string.error_google_sign_in_cancelled
        is androidx.credentials.exceptions.GetCredentialException -> R.string.error_google_sign_in_unavailable

        // ── Lỗi HTTP từ Backend (SDD mục 5.2) ───────────────────────────────
        // 404: tài khoản Firebase chưa được đồng bộ sang backend (SDD INT-API-002)
        is AccountNotSyncedException      -> R.string.error_account_not_synced
        // 409: email đã đăng ký trên hệ thống backend
        is EmailAlreadyExistsException    -> R.string.error_email_already_exists
        // 401 từ backend: token Firebase không hợp lệ / hết hạn
        is BackendUnauthorizedException   -> R.string.error_token_expired
        // 403: không có quyền truy cập
        is ForbiddenException             -> R.string.error_forbidden
        // 400: dữ liệu đầu vào không hợp lệ
        is BadRequestException            -> R.string.error_bad_request
        // 413: file quá lớn (> 10MB)
        is PayloadTooLargeException       -> R.string.error_payload_too_large
        // 500+: lỗi nội bộ server
        is ServerErrorException           -> R.string.error_server
        is InvalidServerResponseException -> R.string.error_server

        // ── Lỗi mạng / kết nối ───────────────────────────────────────────────
        is SocketTimeoutException -> R.string.error_network_timeout
        is TimeoutException       -> R.string.error_network_timeout
        is IOException            -> R.string.error_no_network

        // ── Fallback ──────────────────────────────────────────────────────────
        else -> {
            val msg = message?.lowercase() ?: ""
            when {
                msg.contains("too many") -> R.string.error_too_many_requests
                else                     -> R.string.error_unknown
            }
        }
    }
}
