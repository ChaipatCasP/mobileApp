package com.example.pos.ui.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReportsViewModel : ViewModel() {

    data class StatSummary(
        val revenue: String,
        val orders: String,
        val avgOrder: String
    )

    // Daily revenue data: Mon–Sun (dummy data)
    val dailyRevenue: List<Float> = listOf(1300f, 1600f, 1400f, 1750f, 2200f, 2500f, 1950f)
    val dayLabels: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val summary = StatSummary(
        revenue = "\$12.6k",
        orders = "493",
        avgOrder = "\$25"
    )

    val topProducts: List<TopProduct> = listOf(
        TopProduct(1, "Café Latte",    "Coffee", "https://images.unsplash.com/photo-1561882468-9110e03e0f78?w=400", 4.50),
        TopProduct(2, "Iced Americano","Coffee", "https://images.unsplash.com/photo-1551030173-122aabc4489c?w=400", 4.00),
        TopProduct(3, "Matcha Latte",  "Tea",    "https://images.unsplash.com/photo-1536611641518-a4a945f3c5ca?w=400", 5.00),
        TopProduct(4, "Club Sandwich", "Food",   "https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=400", 6.50),
        TopProduct(5, "Cheesecake",    "Dessert","https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400", 4.50),
    )
}
