package com.example.pos.service.dashboard

import com.google.gson.annotations.SerializedName

/**
 * Response จาก GET /api/dashboard/stats
 */
data class DashboardApiResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("data")    val data: DashboardStats = DashboardStats()
)

/**
 * ข้อมูลสถิติประจำวันสำหรับ Home Dashboard
 */
data class DashboardStats(
    @SerializedName("today_sales")  val todaySales: Double = 0.0,
    @SerializedName("today_orders") val todayOrders: Int = 0,
    @SerializedName("total_tables") val totalTables: Int = 0,
    @SerializedName("occupied_tables") val occupiedTables: Int = 0
)

