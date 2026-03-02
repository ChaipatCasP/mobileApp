package com.example.pos.service.auth

import com.google.gson.annotations.SerializedName

/**
 * Request body สำหรับ POST /api/auth/login
 */
data class LoginRequest(
    /** อีเมลที่ผู้ใช้กรอก */
    @SerializedName("email")
    val email: String,
    /** Password ที่ผู้ใช้กรอก */
    @SerializedName("password")
    val password: String
)
