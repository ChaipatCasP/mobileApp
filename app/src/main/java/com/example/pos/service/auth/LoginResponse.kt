package com.example.pos.service.auth

import com.google.gson.annotations.SerializedName

/**
 * Wrapper ครอบ response จาก API ของ Jagota ทุกตัว
 *
 * top-level JSON:
 * {
 *   "flag": 1,
 *   "message": "",
 *   "package_name": "ws_connectlogin",
 *   "function_name": "GET_USER_LOGIN",
 *   "records": 1,
 *   "result": [ ... ]
 * }
 */
data class LoginApiResponse(
    @SerializedName("flag")
    val flag: Int = 0,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("package_name")
    val packageName: String? = null,

    @SerializedName("function_name")
    val functionName: String? = null,

    @SerializedName("records")
    val records: Int = 0,

    @SerializedName("result")
    val result: List<LoginResultItem> = emptyList()
) {
    val firstResult: LoginResultItem? get() = result.firstOrNull()
}

/**
 * Item ภายใน result[] ของ GET_USER_LOGIN
 *
 * Success → FLAG = "1" + ข้อมูล user ครบ
 * Failure → FLAG = "0" + MSG = error message จาก server
 */
data class LoginResultItem(
    /** "1" = สำเร็จ, "0" = ไม่สำเร็จ */
    @SerializedName("FLAG")
    val flag: String? = null,

    @SerializedName("TYPE")
    val type: String? = null,

    @SerializedName("INTERNAL")
    val internal: String? = null,

    @SerializedName("ICODE")
    val icode: String? = null,

    @SerializedName("CODE")
    val code: String? = null,

    /** ชื่อภาษาอังกฤษ */
    @SerializedName("NAME_E")
    val nameEn: String? = null,

    @SerializedName("STAFF_CODE")
    val staffCode: String? = null,

    @SerializedName("STAFF_NAME")
    val staffName: String? = null,

    @SerializedName("LOGIN")
    val login: String? = null,

    @SerializedName("LOCATION_ID")
    val locationId: String? = null,

    /** Token สำหรับใส่ใน Authorization header ของทุก request ต่อจากนี้ */
    @SerializedName("TOKEN_ID")
    val tokenId: String? = null,

    @SerializedName("EMAIL")
    val email: String? = null,

    @SerializedName("MOBILE")
    val mobile: String? = null,

    @SerializedName("COMPANY")
    val company: String? = null,

    @SerializedName("POSITION_CODE")
    val positionCode: String? = null,

    @SerializedName("POSITION_NAME")
    val positionName: String? = null,

    @SerializedName("CNT_COM")
    val cntCom: String? = null,

    @SerializedName("ICOM")
    val icom: String? = null,

    /** Error message เมื่อ FLAG = "0" */
    @SerializedName("MSG")
    val errorMessage: String? = null
) {
    val isSuccess: Boolean get() = flag == "1"

    /** ชื่อที่แสดงในแอป: nameEn -> staffName -> login -> "" */
    val displayName: String get() = nameEn ?: staffName ?: login ?: ""
}
