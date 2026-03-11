package com.example.pos.ui.inventory

data class InventoryItem(
    val id: Int,
    val name: String,
    val category: String,
    val imageUrl: String,
    val quantity: Int
) {
    enum class StockStatus { IN_STOCK, LIMITED, LOW_STOCK }

    val stockStatus: StockStatus
        get() = when {
            quantity > 20 -> StockStatus.IN_STOCK
            quantity in 5..20 -> StockStatus.LIMITED
            else -> StockStatus.LOW_STOCK
        }

    val stockLabel: String
        get() = when (stockStatus) {
            StockStatus.IN_STOCK  -> "มีสินค้า"
            StockStatus.LIMITED   -> "เหลือน้อย"
            StockStatus.LOW_STOCK -> "สินค้าหมด"
        }
}
