package com.example.pos.service

/**
 * Sealed class ที่ห่อหุ้ม response จาก API ทุกตัว
 * ใช้แทน try/catch และช่วยให้ Handle error แบบ exhaustive
 */
sealed class ApiResult<out T> {

    /** API สำเร็จ — มี data กลับมา */
    data class Success<T>(val data: T) : ApiResult<T>()

    /** API ตอบกลับ แต่เป็น HTTP error (4xx / 5xx) */
    data class HttpError(
        val code: Int,
        val message: String
    ) : ApiResult<Nothing>()

    /** เกิด exception (network ขาด, timeout, parse error ฯลฯ) */
    data class Exception(val throwable: Throwable) : ApiResult<Nothing>()
}

/** Helper — ดึง data ถ้า Success, ไม่งั้น null */
fun <T> ApiResult<T>.getOrNull(): T? = if (this is ApiResult.Success) data else null

/** Helper — throw ถ้าเป็น error */
fun <T> ApiResult<T>.getOrThrow(): T = when (this) {
    is ApiResult.Success -> data
    is ApiResult.HttpError -> error("HTTP $code: $message")
    is ApiResult.Exception -> throw throwable
}
