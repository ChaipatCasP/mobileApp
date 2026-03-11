package com.example.pos.service.dashboard

import com.example.pos.service.ApiClient
import com.example.pos.service.ApiResult
import com.example.pos.service.BaseApiService

/**
 * Service สำหรับดึงข้อมูล Dashboard (ยอดขาย / ออเดอร์วันนี้)
 *
 * API: GET https://nodeapipos.baby-pat-tac.workers.dev/api/dashboard/stats
 */
class DashboardService : BaseApiService() {

    override val baseUrl = ApiClient.AUTH_BASE_URL

    /**
     * ดึงสถิติ Dashboard ประจำวัน
     * GET /api/dashboard/stats
     */
    suspend fun getStats(): ApiResult<DashboardStats> {
        val result = get<DashboardApiResponse>(
            endpoint = "/api/dashboard/stats",
            type = type<DashboardApiResponse>()
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

