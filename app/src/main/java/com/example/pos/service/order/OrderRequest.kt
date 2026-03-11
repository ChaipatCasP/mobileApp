package com.example.pos.service.order

import com.google.gson.annotations.SerializedName

/**
 * Request body สำหรับสร้างออเดอร์ใหม่
 * POST /api/orders
 */
data class OrderRequest(
    @SerializedName("table_id")    val tableId: Int,
    @SerializedName("items")       val items: List<OrderItemRequest>,
    @SerializedName("payment")     val payment: String,   // "cash" | "card" | "promptpay"
    @SerializedName("total")       val total: Double
)

data class OrderItemRequest(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("name")       val name: String,
    @SerializedName("quantity")   val quantity: Int,
    @SerializedName("price")      val price: Double,
    @SerializedName("subtotal")   val subtotal: Double
)

/**
 * Response จาก POST /api/orders
 */
data class OrderApiResponse(
    @SerializedName("success")  val success: Boolean = false,
    @SerializedName("order_id") val orderId: Int = 0,
    @SerializedName("message")  val message: String = ""
)

