package com.example.pos.service.table

import com.google.gson.annotations.SerializedName

/**
 * Status ของโต๊ะ
 */
enum class TableStatus {
    AVAILABLE, OCCUPIED, RESERVED, DIRTY;

    companion object {
        /** Parse แบบ case-insensitive เพราะ API คืน "Available" / "Occupied" / "Reserved" / "Dirty" */
        fun from(raw: String?): TableStatus = when (raw?.lowercase()) {
            "occupied"  -> OCCUPIED
            "reserved"  -> RESERVED
            "dirty"     -> DIRTY
            else        -> AVAILABLE
        }
    }
}

/**
 * Wrapper response จาก GET /api/tables
 * { "success": true, "data": [ ... ] }
 */
data class TableApiResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("data")    val data: List<TableModel> = emptyList()
)

/**
 * Model สำหรับข้อมูลโต๊ะ ที่ได้จาก GET /api/tables
 *
 * ตัวอย่าง JSON:
 * {
 *   "table_info_id": 1,
 *   "code": "T01",
 *   "name_en": "Table 01 (Indoor)",
 *   "name_th": "โต๊ะ 01 (ในร่ม)",
 *   "total_sit": 4,
 *   "picture": "https://...",
 *   "table_status": "occupied"
 * }
 */
data class TableModel(
    @SerializedName("table_info_id") val id: Int = 0,
    @SerializedName("code")          val code: String = "",
    @SerializedName("name_en")       val nameEn: String = "",
    @SerializedName("name_th")       val nameTh: String = "",
    @SerializedName("total_sit")     val totalSit: Int = 0,
    @SerializedName("picture")       val picture: String = "",
    @SerializedName("table_status")  val tableStatusRaw: String = "Available"
) {
    /** TableStatus enum — parse จาก string แบบ case-insensitive */
    val status: TableStatus get() = TableStatus.from(tableStatusRaw)
}
