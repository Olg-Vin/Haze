package com.vinio.haze.domain.exceptions

sealed class AppError : Throwable() {
    object NetworkUnavailable : AppError()
    data class ApiError(val code: Int, override val message: String) : AppError()
    data class Unexpected(val original: Throwable) : AppError()
}
