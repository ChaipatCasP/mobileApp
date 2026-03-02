package com.example.pos.service.auth

import com.google.gson.annotations.SerializedName

/**
 * Response จาก POST /api/auth/login
 *
 * Success JSON:
 * {
 *   "success": true,
 *   "message": "เข้าสู่ระบบสำเร็จ",
 *   "access_token": "...",
 *   "refresh_token": "...",
 *   "session_id": "...",
 *   "user": { "user_id": "...", "email": "...", "status": "ACTIVE", "roles": [] }
 * }
 *
 * Error JSON:
 * { "success": false, "message": "email หรือ password ไม่ถูกต้อง" }
 */
data class LoginApiResponse(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("access_token")
    val accessToken: String? = null,

    @SerializedName("refresh_token")
    val refreshToken: String? = null,

    @SerializedName("session_id")
    val sessionId: String? = null,

    @SerializedName("user")
    val user: LoginUser? = null
) {
    /** true เมื่อ login สำเร็จ และมี access_token */
    val isSuccess: Boolean get() = success && accessToken != null

    /** ชื่อผู้ใช้งานที่แสดงในแอป */
    val displayName: String get() = user?.email ?: ""
}

/**
 * ข้อมูล user ที่ได้จาก response ของ /api/auth/login
 */
data class LoginUser(
    @SerializedName("user_id")
    val userId: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("roles")
    val roles: List<String> = emptyList()
)
