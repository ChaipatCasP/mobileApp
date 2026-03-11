package com.example.pos.service.product

import com.example.pos.service.ApiClient
import com.example.pos.service.ApiResult
import com.example.pos.service.BaseApiService

/**
 * Service สำหรับดึงข้อมูลสินค้า/เมนู
 *
 * API: GET https://nodeapipos.baby-pat-tac.workers.dev/api/products
 *
 * Usage:
 * ```
 * viewModelScope.launch {
 *     when (val result = ProductService().getProducts()) {
 *         is ApiResult.Success   -> { result.data /* List<ProductApiModel> */ }
 *         is ApiResult.HttpError -> { result.code, result.message }
 *         is ApiResult.Exception -> { result.throwable }
 *     }
 * }
 * ```
 */
class ProductService : BaseApiService() {

    override val baseUrl = ApiClient.AUTH_BASE_URL

    /**
     * ดึงรายการสินค้า/เมนูทั้งหมด
     * GET /api/products
     */
    suspend fun getProducts(): ApiResult<List<ProductApiModel>> {
        val result = get<ProductApiResponse>(
            endpoint = "/api/products",
            type = type<ProductApiResponse>()
        )
        return when (result) {
            is ApiResult.Success -> {
                if (result.data.success) {
                    ApiResult.Success(result.data.data)
                } else {
                    ApiResult.HttpError(200, "API returned success=false")
                }
            }
            is ApiResult.HttpError -> result
            is ApiResult.Exception -> result
        }
    }
}

