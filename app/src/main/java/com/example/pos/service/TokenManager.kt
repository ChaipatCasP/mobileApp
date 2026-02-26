package com.example.pos.service

import android.content.Context
import android.content.SharedPreferences

/**
 * จัดการ Auth Token ด้วย SharedPreferences
 * ต้อง call [init] ก่อนใช้งาน (เรียกใน Application.onCreate หรือ MainActivity)
 */
object TokenManager {

    private const val PREF_NAME = "pos_prefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_USER_NAME = "user_name"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String = prefs.getString(KEY_TOKEN, "") ?: ""

    fun saveUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""

    fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).remove(KEY_USER_NAME).apply()
    }

    fun isLoggedIn(): Boolean = getToken().isNotEmpty()
}
