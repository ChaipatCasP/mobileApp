package com.example.pos.ui.order

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String,
    /** URL รูปภาพสินค้า */
    val imageUrl: String
) {
    val priceFormatted: String get() = "$${"%.2f".format(price)}"
}
