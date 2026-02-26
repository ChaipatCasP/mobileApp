package com.example.pos.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Singleton OkHttpClient
 * - Logging interceptor (DEBUG เท่านั้น)
 * - Auth token interceptor (ดึง token จาก TokenManager อัตโนมัติ)
 * - Timeout มาตรฐาน 30 วินาที
 */
object ApiClient {

    const val BASE_URL = "https://api-staging.jagota.com/Apip"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                // ใส่ Authorization header อัตโนมัติทุก request
                val token = TokenManager.getToken()
                val request = chain.request().newBuilder().apply {
                    if (token.isNotEmpty()) {
                        header("Authorization", token)
                    }
                }.build()
                chain.proceed(request)
            }
            .build()
    }
}
