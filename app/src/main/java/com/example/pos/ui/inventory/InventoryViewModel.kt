package com.example.pos.ui.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InventoryViewModel : ViewModel() {

    companion object {
        val ALL_ITEMS = listOf(
            InventoryItem(1,  "Café Latte",      "Coffee",  "https://images.unsplash.com/photo-1561882468-9110e03e0f78?w=400", 120),
            InventoryItem(2,  "Croissant",        "Food",    "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=400", 8),
            InventoryItem(3,  "Matcha Latte",     "Tea",     "https://images.unsplash.com/photo-1536611641518-a4a945f3c5ca?w=400", 85),
            InventoryItem(4,  "Brownie",          "Dessert", "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=400", 3),
            InventoryItem(5,  "Iced Americano",   "Coffee",  "https://images.unsplash.com/photo-1551030173-122aabc4489c?w=400", 95),
            InventoryItem(6,  "Club Sandwich",    "Food",    "https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=400", 25),
            InventoryItem(7,  "Earl Grey Tea",    "Tea",     "https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?w=400", 40),
            InventoryItem(8,  "Cheesecake",       "Dessert", "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400", 12),
            InventoryItem(9,  "Cappuccino",       "Coffee",  "https://images.unsplash.com/photo-1534778101976-62847782c213?w=400", 2),
            InventoryItem(10, "Oolong Tea",       "Tea",     "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400", 60),
        )
    }

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _filteredItems = MutableLiveData(ALL_ITEMS)
    val filteredItems: LiveData<List<InventoryItem>> = _filteredItems

    val totalCount: Int get() = ALL_ITEMS.size

    fun search(query: String) {
        _searchQuery.value = query
        _filteredItems.value = if (query.isBlank()) ALL_ITEMS
        else ALL_ITEMS.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.category.contains(query, ignoreCase = true)
        }
    }
}
