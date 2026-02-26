package com.example.pos.service.auth

import com.example.pos.service.ApiResult
import com.example.pos.service.BaseApiService
import com.example.pos.service.TokenManager

/**
 * Service จัดการ Authentication API
 */
class AuthService : BaseApiService() {

    /**
     * Login ด้วย GET_USER_LOGIN
     *
     * Return:
     * - ApiResult.Success(LoginResultItem) — FLAG = "1" พร้อม token และข้อมูล user
     * - ApiResult.HttpError                — HTTP error เช่น 4xx/5xx
     * - ApiResult.Exception                — network error หรือ parse error
     *
     * ตัวอย่าง:
     * ```
     * viewModelScope.launch {
     *     when (val r = AuthService().login(LoginRequest(username = "JBT04", password = "BHARAT09"))) {
     *         is ApiResult.Success  -> { r.data.tokenId ... }
     *         is ApiResult.HttpError -> { r.code, r.message }
     *         is ApiResult.Exception -> { r.throwable }
     *     }
     * }
     * ```
     */
    suspend fun login(request: LoginRequest): ApiResult<LoginResultItem> {
        val params = mapOf(
            "P_COM"   to request.company,
            "P_USER"  to request.apiUser,
            "P_KEY"   to request.apiKey,
            "P_LOGIN" to request.username,
            "P_PWD"   to request.password
        )

        // Parse wrapper ก่อน
        val apiResult = postMultipart<LoginApiResponse>(
            endpoint = "/ws_connectlogin/GET_USER_LOGIN",
            params = params,
            type = type<LoginApiResponse>()
        )

        return when (apiResult) {
            is ApiResult.Success -> {
                val item = apiResult.data.firstResult
                if (item != null && item.isSuccess) {
                    // บันทึก token + ข้อมูล user
                    item.tokenId?.let { TokenManager.saveToken(it) }
                    item.displayName.let { name ->
                        if (name.isNotEmpty()) TokenManager.saveUserName(name)
                    }
                    ApiResult.Success(item)
                } else {
                    // FLAG = "0" — business-level error (wrong user/password)
                    val msg = item?.errorMessage ?: "Login failed"
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
