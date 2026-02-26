package com.example.pos.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Singleton จัดการตะกร้าสินค้า
 * ใช้ LiveData เพื่อให้ UI update อัตโนมัติ
 */
object CartManager {

    private val _items = MutableLiveData<List<CartItem>>(emptyList())
    val items: LiveData<List<CartItem>> = _items

    private val currentItems: MutableList<CartItem>
        get() = _items.value?.toMutableList() ?: mutableListOf()

    val totalAmount: Double
        get() = _items.value?.sumOf { it.subtotal } ?: 0.0

    val totalAmountFormatted: String
        get() = "$${"%.2f".format(totalAmount)}"

    val totalCount: Int
        get() = _items.value?.sumOf { it.quantity } ?: 0

    fun addItem(product: Product) {
        val list = currentItems
        val existing = list.find { it.product.id == product.id }
        if (existing != null) {
            existing.quantity++
        } else {
            list.add(CartItem(product))
        }
        _items.value = list
    }

    fun increaseQuantity(product: Product) {
        val list = currentItems
        list.find { it.product.id == product.id }?.let { it.quantity++ }
        _items.value = list
    }

    fun decreaseQuantity(product: Product) {
        val list = currentItems
        val item = list.find { it.product.id == product.id }
        if (item != null) {
            if (item.quantity > 1) item.quantity--
            else list.remove(item)
        }
        _items.value = list
    }

    fun getQuantity(product: Product): Int =
        _items.value?.find { it.product.id == product.id }?.quantity ?: 0

    fun clearCart() {
        _items.value = emptyList()
    }
}
