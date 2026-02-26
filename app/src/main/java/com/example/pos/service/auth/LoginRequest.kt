package com.example.pos.service.auth

import com.google.gson.annotations.SerializedName

/**
 * request body สำหรับ GET_USER_LOGIN
 */
data class LoginRequest(
    /** รหัสบริษัท เช่น "JB" */
    val company: String = "JB",
    /** ชื่อ User สำหรับ API */
    val apiUser: String = "DEMO",
    /** API Key (ถ้ามี) */
    val apiKey: String = "",
    /** Username ที่ผู้ใช้กรอก */
    val username: String,
    /** Password ที่ผู้ใช้กรอก */
    val password: String
)
