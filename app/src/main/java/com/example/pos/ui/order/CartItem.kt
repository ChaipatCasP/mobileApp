package com.example.pos.ui.order

data class CartItem(
    val product: Product,
    var quantity: Int = 1
) {
    val subtotal: Double get() = product.price * quantity
    val subtotalFormatted: String get() = "$${"%.2f".format(subtotal)}"
}
