package com.example.pos.service.table

import com.example.pos.service.ApiClient
import com.example.pos.service.ApiResult
import com.example.pos.service.BaseApiService

/**
 * Service สำหรับจัดการโต๊ะ
 *
 * API: GET https://nodeapipos.baby-pat-tac.workers.dev/api/tables
 *
 * Usage:
 * ```
 * viewModelScope.launch {
 *     when (val result = TableService().getTables()) {
 *         is ApiResult.Success  -> { result.data /* List<TableModel> */ }
 *         is ApiResult.HttpError -> { result.code, result.message }
 *         is ApiResult.Exception -> { result.throwable }
 *     }
 * }
 * ```
 */
class TableService : BaseApiService() {

    override val baseUrl = ApiClient.AUTH_BASE_URL

    /**
     * ดึงรายการโต๊ะทั้งหมด
     * GET /api/tables
     * Response: { "success": true, "data": [ ... ] }
     */
    suspend fun getTables(): ApiResult<List<TableModel>> {
        val result = get<TableApiResponse>(
            endpoint = "/api/tables",
            type = type<TableApiResponse>()
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
