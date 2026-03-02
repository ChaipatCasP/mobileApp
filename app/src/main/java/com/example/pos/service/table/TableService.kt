package com.example.pos.service.table

import com.example.pos.service.ApiResult
import com.example.pos.service.BaseApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request

/**
 * Service สำหรับจัดการโต๊ะ
 *
 * API: http://localhost:3000/api/tables
 * (ในอีมูเลเตอร์ Android ใช้ 10.0.2.2 แทน localhost)
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

    companion object {
        // สำหรับ Android Emulator: 10.0.2.2 = host machine's localhost
        // สำหรับ Real Device เปลี่ยนเป็น IP จริงของ server เช่น "http://192.168.1.x:3000"
        const val TABLE_API_BASE_URL = "http://10.0.2.2:3000"
    }

    /**
     * ดึงรายการโต๊ะทั้งหมด
     * GET /api/tables
     * Response: { "success": true, "data": [ ... ] }
     */
    suspend fun getTables(): ApiResult<List<TableModel>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$TABLE_API_BASE_URL/api/tables")
                .header("accept", "application/json")
                .get()
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyString = response.body?.string() ?: "{}"
                val wrapper = gson.fromJson(bodyString, TableApiResponse::class.java)
                if (wrapper.success) {
                    ApiResult.Success(wrapper.data)
                } else {
                    ApiResult.HttpError(200, "API returned success=false")
                }
            } else {
                ApiResult.HttpError(response.code, response.message)
            }
        } catch (e: Throwable) {
            ApiResult.Exception(e)
        }
    }
}
