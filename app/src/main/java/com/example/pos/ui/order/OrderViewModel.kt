package com.example.pos.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos.service.ApiResult
import com.example.pos.service.product.ProductService
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {

    companion object {
        val CATEGORIES = listOf("All", "Coffee", "Tea", "Food", "Dessert")

        /** Fallback mock data ใช้เมื่อ API ไม่พร้อมใช้งาน */
        val MOCK_PRODUCTS = listOf(
            Product(1,  "Café Latte",      120.0, "Coffee",  "https://images.unsplash.com/photo-1561882468-9110e03e0f78?w=400"),
            Product(2,  "Croissant",        65.0, "Food",    "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=400"),
            Product(3,  "Matcha Latte",    140.0, "Tea",     "https://images.unsplash.com/photo-1536611641518-a4a945f3c5ca?w=400"),
            Product(4,  "Brownie",          85.0, "Dessert", "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=400"),
            Product(5,  "Americano",        90.0, "Coffee",  "https://images.unsplash.com/photo-1551030173-122aabc4489c?w=400"),
            Product(6,  "Earl Grey Tea",   110.0, "Tea",     "https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?w=400"),
            Product(7,  "Toast & Butter",   55.0, "Food",    "https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=400"),
            Product(8,  "Cheesecake",      120.0, "Dessert", "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400"),
            Product(9,  "Cappuccino",      110.0, "Coffee",  "https://images.unsplash.com/photo-1534778101976-62847782c213?w=400"),
            Product(10, "Oolong Tea",      120.0, "Tea",     "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400"),
        )
    }

    private val productService = ProductService()

    private val _allProducts = MutableLiveData<List<Product>>(MOCK_PRODUCTS)

    private val _selectedCategory = MutableLiveData("All")
    val selectedCategory: LiveData<String> = _selectedCategory

    private val _filteredProducts = MutableLiveData<List<Product>>(MOCK_PRODUCTS)
    val filteredProducts: LiveData<List<Product>> = _filteredProducts

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        loadProducts()
    }

    /**
     * โหลดสินค้าจาก API — ถ้า API ไม่พร้อม ใช้ MOCK_PRODUCTS แทน
     */
    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = productService.getProducts()) {
                is ApiResult.Success -> {
                    val products = result.data.map { m ->
                        Product(
                            id       = m.id,
                            name     = m.nameEn.ifBlank { m.nameTh },
                            price    = m.price,
                            category = m.category,
                            imageUrl = m.picture
                        )
                    }
                    if (products.isNotEmpty()) {
                        _allProducts.value = products
                        applyFilter(_selectedCategory.value ?: "All")
                    }
                }
                is ApiResult.HttpError, is ApiResult.Exception -> {
                    // Silent fallback: ใช้ MOCK_PRODUCTS โดยไม่แสดง error
                    _allProducts.value = MOCK_PRODUCTS
                    applyFilter(_selectedCategory.value ?: "All")
                }
            }

            _isLoading.value = false
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        applyFilter(category)
    }

    private fun applyFilter(category: String) {
        val all = _allProducts.value ?: MOCK_PRODUCTS
        _filteredProducts.value = if (category == "All") all
        else all.filter { it.category.equals(category, ignoreCase = true) }
    }
}
