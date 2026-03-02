package com.example.pos.service.auth

import com.example.pos.service.ApiClient
import com.example.pos.service.ApiResult
import com.example.pos.service.BaseApiService
import com.example.pos.service.TokenManager

/**
 * Service จัดการ Authentication API
 * เชื่อมต่อ POST /api/auth/login ที่ ${ApiClient.AUTH_BASE_URL}
 */
class AuthService : BaseApiService() {

    /** ใช้ AUTH_BASE_URL แทน BASE_URL เดิม */
    override val baseUrl = ApiClient.AUTH_BASE_URL

    /**
     * Login ด้วย email + password
     *
     * Return:
     * - ApiResult.Success(LoginApiResponse) — success=true พร้อม access_token
     * - ApiResult.HttpError                 — HTTP error เช่น 4xx/5xx
     * - ApiResult.Exception                 — network error หรือ parse error
     *
     * ตัวอย่าง:
     * ```
     * viewModelScope.launch {
     *     when (val r = AuthService().login(LoginRequest(email = "admin@pos.com", password = "admin1234"))) {
     *         is ApiResult.Success  -> { r.data.accessToken ... }
     *         is ApiResult.HttpError -> { r.code, r.message }
     *         is ApiResult.Exception -> { r.throwable }
     *     }
     * }
     * ```
     */
    suspend fun login(request: LoginRequest): ApiResult<LoginApiResponse> {
        val apiResult = postJson<LoginApiResponse>(
            endpoint = "/api/auth/login",
            bodyObject = request,
            type = type<LoginApiResponse>()
        )

        return when (apiResult) {
            is ApiResult.Success -> {
                val data = apiResult.data
                if (data.isSuccess) {
                    // บันทึก token + ข้อมูล user
                    data.accessToken?.let { TokenManager.saveToken(it) }
                    data.displayName.let { name ->
                        if (name.isNotEmpty()) TokenManager.saveUserName(name)
                    }
                    ApiResult.Success(data)
                } else {
                    // success=false — ผิด email/password
                    val msg = data.message ?: "Login failed"
                    ApiResult.HttpError(code = 401, message = msg)
                }
            }
            is ApiResult.HttpError -> apiResult
            is ApiResult.Exception -> apiResult
        }
    }

    /** Logout — ลบ token และข้อมูล user ออกจากเครื่อง */
    fun logout() {
        TokenManager.clearToken()
    }
}
