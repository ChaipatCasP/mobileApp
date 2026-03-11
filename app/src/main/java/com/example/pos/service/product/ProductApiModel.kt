package com.example.pos.service.product

import com.google.gson.annotations.SerializedName

/**
 * Wrapper response จาก GET /api/products
 * { "success": true, "data": [ ... ] }
 */
data class ProductApiResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("data")    val data: List<ProductApiModel> = emptyList()
)

/**
 * Model ข้อมูลสินค้าจาก API
 */
data class ProductApiModel(
    @SerializedName("product_id")  val id: Int = 0,
    @SerializedName("name_en")     val nameEn: String = "",
    @SerializedName("name_th")     val nameTh: String = "",
    @SerializedName("category")    val category: String = "",
    @SerializedName("price")       val price: Double = 0.0,
    @SerializedName("picture")     val picture: String = "",
    @SerializedName("stock")       val stock: Int = 0
)

