package com.example.pos.service.order

import com.example.pos.service.ApiClient
import com.example.pos.service.ApiResult
import com.example.pos.service.BaseApiService

/**
 * Service สำหรับจัดการออเดอร์
 *
 * POST /api/orders — บันทึกออเดอร์ใหม่
 */
class OrderService : BaseApiService() {

    override val baseUrl = ApiClient.AUTH_BASE_URL

    /**
     * สร้างออเดอร์ใหม่ และส่งข้อมูลไปยัง API
     *
     * @param request ข้อมูลออเดอร์ (โต๊ะ, รายการสินค้า, วิธีชำระ, ยอดรวม)
     * @return ApiResult.Success พร้อม OrderApiResponse
     */
    suspend fun createOrder(request: OrderRequest): ApiResult<OrderApiResponse> {
        return postJson(
            endpoint = "/api/orders",
            bodyObject = request,
            type = type<OrderApiResponse>()
        )
    }
}

