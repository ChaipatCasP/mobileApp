package com.example.pos.ui.login

/**
 * Authentication result: success (user details) หรือ error
 * - error: @StringRes สำหรับ error ทั่วไป (network, etc)
 * - errorMessage: String จาก API (เช่น "Cannot find User...")
 */
data class LoginResult(
    val success: LoggedInUserView? = null,
    val error: Int? = null,
    val errorMessage: String? = null
)