package com.example.pos.ui.reports

data class TopProduct(
    val rank: Int,
    val name: String,
    val category: String,
    val imageUrl: String,
    val price: Double
) {
    val priceFormatted: String get() = "$${"%.2f".format(price)}"
    val rankLabel: String get() = "#$rank"
}
