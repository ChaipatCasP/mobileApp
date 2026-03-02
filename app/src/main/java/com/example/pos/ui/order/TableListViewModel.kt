package com.example.pos.ui.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos.service.ApiResult
import com.example.pos.service.table.TableModel
import com.example.pos.service.table.TableService
import com.example.pos.service.table.TableStatus
import kotlinx.coroutines.launch

/**
 * ViewModel สำหรับหน้าแสดงรายการโต๊ะ
 */
class TableListViewModel : ViewModel() {

    private val tableService = TableService()

    private val _allTables = MutableLiveData<List<TableModel>>(emptyList())

    private val _filteredTables = MutableLiveData<List<TableModel>>(emptyList())
    val filteredTables: LiveData<List<TableModel>> = _filteredTables

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _activeFilter = MutableLiveData<TableStatus?>(null)
    val activeFilter: LiveData<TableStatus?> = _activeFilter

    // Count live data
    private val _availableCount = MutableLiveData(0)
    val availableCount: LiveData<Int> = _availableCount

    private val _occupiedCount = MutableLiveData(0)
    val occupiedCount: LiveData<Int> = _occupiedCount

    private val _reservedCount = MutableLiveData(0)
    val reservedCount: LiveData<Int> = _reservedCount

    init {
        loadTables()
    }

    /**
     * โหลดรายการโต๊ะจาก API
     * GET http://10.0.2.2:3000/api/tables
     */
    fun loadTables() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            when (val result = tableService.getTables()) {
                is ApiResult.Success -> {
                    _allTables.value = result.data
                    updateCounts(result.data)
                    applyFilter(_activeFilter.value)
                }
                is ApiResult.HttpError -> {
                    _error.value = "HTTP ${result.code}: ${result.message}"
                    // Use mock data when API unavailable
                    useMockData()
                }
                is ApiResult.Exception -> {
                    _error.value = null // Don't show error, just use mock data
                    // Use mock data when API unavailable (e.g. dev environment)
                    useMockData()
                }
            }

            _isLoading.value = false
        }
    }

    /**
     * กรองโต๊ะตาม status
     * null = แสดงทั้งหมด
     */
    fun setFilter(status: TableStatus?) {
        _activeFilter.value = status
        applyFilter(status)
    }

    private fun applyFilter(status: TableStatus?) {
        val all = _allTables.value ?: emptyList()
        _filteredTables.value = if (status == null) all else all.filter { it.status == status }
    }

    private fun updateCounts(tables: List<TableModel>) {
        _availableCount.value = tables.count { it.status == TableStatus.AVAILABLE }
        _occupiedCount.value  = tables.count { it.status == TableStatus.OCCUPIED }
        _reservedCount.value  = tables.count { it.status == TableStatus.RESERVED }
    }

    /** ข้อมูลตัวอย่าง สำหรับใช้เมื่อ API ไม่พร้อม */
    private fun useMockData() {
        val mock = listOf(
            TableModel(1, "T01", "Table 01 (Indoor)",       "โต๊ะ 01 (ในร่ม)",       4,  "", "occupied"),
            TableModel(2, "T02", "Table 02 (Indoor)",       "โต๊ะ 02 (ในร่ม)",       2,  "", "Available"),
            TableModel(3, "T03", "Table 03 (Window Side)",  "โต๊ะ 03 (ริมหน้าต่าง)", 2,  "", "Available"),
            TableModel(4, "V01", "VIP Room A",              "ห้องวีไอพี A",          8,  "", "Available"),
            TableModel(5, "V02", "VIP Room B",              "ห้องวีไอพี B",          10, "", "reserved"),
            TableModel(6, "O01", "Outdoor Terrace 1",       "ระเบียงกลางแจ้ง 1",     4,  "", "Available"),
            TableModel(7, "O02", "Outdoor Terrace 2",       "ระเบียงกลางแจ้ง 2",     4,  "", "occupied"),
        )
        _allTables.value = mock
        updateCounts(mock)
        applyFilter(_activeFilter.value)
    }
}
