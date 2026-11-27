package com.example.proyek_papb_fe.core.data.remote

data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)

data class PageDto<T>(
    val items: List<T>?,
    val limit: Int,
    val offset: Int,
    val hasMore: Boolean
)

internal data class ApiErrorBody(
    val message: String?,
    val code: String?,
    val errors: List<FieldErrorDto>?
)

internal data class FieldErrorDto(
    val field: String?,
    val message: String?
)
