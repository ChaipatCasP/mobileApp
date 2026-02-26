package com.example.pos.service

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type

/**
 * Base class สำหรับ API Service ทุกตัว
 *
 * - ใช้ ApiClient.httpClient ส่ว่งกลาง
 * - ทุก call รัน Coroutine บน Dispatchers.IO
 * - Parse JSON response ด้วย Gson อัตโนมัติ
 * - ห่อ response ด้วย ApiResult
 */
abstract class BaseApiService {

    protected val client = ApiClient.httpClient
    protected val gson = Gson()
    protected val baseUrl = ApiClient.BASE_URL

    /**
     * ส่ง Multipart/Form-Data POST request แล้ว parse body เป็น [T]
     *
     * @param endpoint  path ต่อจาก baseUrl เช่น "/ws_connectlogin/GET_USER_LOGIN"
     * @param params    Map ของ form fields
     * @param type      TypeToken สำหรับ Gson parse (ใช้ inline reified แทนได้)
     */
    protected suspend fun <T> postMultipart(
        endpoint: String,
        params: Map<String, String>,
        type: Type
    ): ApiResult<T> = withContext(Dispatchers.IO) {
        try {
            val body = MultipartBody.Builder().setType(MultipartBody.FORM).apply {
                params.forEach { (key, value) -> addFormDataPart(key, value) }
            }.build()

            val request = Request.Builder()
                .url("$baseUrl$endpoint")
                .post(body)
                .build()

            executeRequest(request, type)
        } catch (e: Throwable) {
            ApiResult.Exception(e)
        }
    }

    /**
     * ส่ง x-www-form-urlencoded POST request แล้ว parse body เป็น [T]
     */
    protected suspend fun <T> postForm(
        endpoint: String,
        params: Map<String, String>,
        type: Type
    ): ApiResult<T> = withContext(Dispatchers.IO) {
        try {
            val body = FormBody.Builder().apply {
                params.forEach { (key, value) -> add(key, value) }
            }.build()

            val request = Request.Builder()
                .url("$baseUrl$endpoint")
                .post(body)
                .build()

            executeRequest(request, type)
        } catch (e: Throwable) {
            ApiResult.Exception(e)
        }
    }

    /**
     * ส่ง JSON POST request แล้ว parse body เป็น [T]
     */
    protected suspend fun <T> postJson(
        endpoint: String,
        bodyObject: Any,
        type: Type
    ): ApiResult<T> = withContext(Dispatchers.IO) {
        try {
            val requestBody: RequestBody = gson.toJson(bodyObject)
                .toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$baseUrl$endpoint")
                .post(requestBody)
                .build()

            executeRequest(request, type)
        } catch (e: Throwable) {
            ApiResult.Exception(e)
        }
    }

    /**
     * ส่ง GET request แล้ว parse body เป็น [T]
     * @param queryParams Map ของ query string (?key=value)
     */
    protected suspend fun <T> get(
        endpoint: String,
        queryParams: Map<String, String> = emptyMap(),
        type: Type
    ): ApiResult<T> = withContext(Dispatchers.IO) {
        try {
            val urlBuilder = StringBuilder("$baseUrl$endpoint")
            if (queryParams.isNotEmpty()) {
                urlBuilder.append("?")
                urlBuilder.append(queryParams.entries.joinToString("&") { "${it.key}=${it.value}" })
            }

            val request = Request.Builder()
                .url(urlBuilder.toString())
                .get()
                .build()

            executeRequest(request, type)
        } catch (e: Throwable) {
            ApiResult.Exception(e)
        }
    }

    // ---- Private helpers ----

    private fun <T> executeRequest(request: Request, type: Type): ApiResult<T> {
        val response = client.newCall(request).execute()
        return if (response.isSuccessful) {
            val bodyString = response.body?.string() ?: ""
            val parsed: T = gson.fromJson(bodyString, type)
            ApiResult.Success(parsed)
        } else {
            ApiResult.HttpError(response.code, response.message)
        }
    }

    /** Convenience: สร้าง Type สำหรับ List<T> */
    protected inline fun <reified T> listType(): Type =
        object : TypeToken<List<T>>() {}.type

    /** Convenience: สร้าง Type สำหรับ T */
    protected inline fun <reified T> type(): Type =
        object : TypeToken<T>() {}.type
}
