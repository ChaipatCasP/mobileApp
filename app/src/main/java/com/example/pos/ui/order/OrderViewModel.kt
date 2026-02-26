package com.example.pos.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrderViewModel : ViewModel() {

    companion object {
        val CATEGORIES = listOf("All", "Coffee", "Tea", "Food", "Dessert")

        /** Dummy product data — แทนที่ด้วย API จริง */
        val ALL_PRODUCTS = listOf(
            Product(1, "Café Latte",      4.50, "Coffee",  "https://images.unsplash.com/photo-1561882468-9110e03e0f78?w=400"),
            Product(2, "Croissant",       3.00, "Food",    "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=400"),
            Product(3, "Matcha Latte",    5.00, "Tea",     "https://images.unsplash.com/photo-1536611641518-a4a945f3c5ca?w=400"),
            Product(4, "Brownie",         3.50, "Dessert", "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=400"),
            Product(5, "Americano",       3.50, "Coffee",  "https://images.unsplash.com/photo-1551030173-122aabc4489c?w=400"),
            Product(6, "Earl Grey Tea",   4.00, "Tea",     "https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?w=400"),
            Product(7, "Toast & Butter",  2.50, "Food",    "https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=400"),
            Product(8, "Cheesecake",      4.50, "Dessert", "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400"),
            Product(9, "Cappuccino",      4.00, "Coffee",  "https://images.unsplash.com/photo-1534778101976-62847782c213?w=400"),
            Product(10,"Oolong Tea",      4.50, "Tea",     "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"),
        )
    }

    private val _selectedCategory = MutableLiveData("All")
    val selectedCategory: LiveData<String> = _selectedCategory

    private val _filteredProducts = MutableLiveData(ALL_PRODUCTS)
    val filteredProducts: LiveData<List<Product>> = _filteredProducts

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        _filteredProducts.value = if (category == "All") ALL_PRODUCTS
        else ALL_PRODUCTS.filter { it.category == category }
    }
}
