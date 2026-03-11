package com.example.pos.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos.service.ApiResult
import com.example.pos.service.dashboard.DashboardService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeViewModel : ViewModel() {

    private val dashboardService = DashboardService()

    private val _greeting = MutableLiveData<String>()
    val greeting: LiveData<String> = _greeting

    private val _todayDate = MutableLiveData<String>()
    val todayDate: LiveData<String> = _todayDate

    private val _todaySales = MutableLiveData<String>().apply { value = "฿0" }
    val todaySales: LiveData<String> = _todaySales

    private val _todayOrders = MutableLiveData<Int>().apply { value = 0 }
    val todayOrders: LiveData<Int> = _todayOrders

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        refreshData()
    }

    fun refreshData() {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        _greeting.value = when {
            hour < 12 -> "สวัสดีตอนเช้า ☕"
            hour < 17 -> "สวัสดีตอนบ่าย ☀️"
            else      -> "สวัสดีตอนเย็น 🌙"
        }
        val fmt = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("th", "TH"))
        _todayDate.value = fmt.format(cal.time)

        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = dashboardService.getStats()) {
                is ApiResult.Success -> {
                    val stats = result.data
                    _todaySales.value  = "฿${"%.0f".format(stats.todaySales)}"
                    _todayOrders.value = stats.todayOrders
                }
                is ApiResult.HttpError, is ApiResult.Exception -> {
                    // ใช้ค่า default ไม่แสดง error บน Home
                }
            }
            _isLoading.value = false
        }
    }
}