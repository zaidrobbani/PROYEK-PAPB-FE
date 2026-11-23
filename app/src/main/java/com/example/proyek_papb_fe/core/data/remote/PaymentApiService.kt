package com.example.proyek_papb_fe.core.data.remote

import com.example.proyek_papb_fe.core.data.remote.dto.PaymentDetailDto
import com.example.proyek_papb_fe.core.data.remote.dto.PaymentHistoryDto
import com.example.proyek_papb_fe.core.data.remote.dto.SimulatePaymentRequest
import com.example.proyek_papb_fe.core.data.remote.dto.WalletDto
import com.example.proyek_papb_fe.core.data.remote.dto.WalletEntryDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** Pembayaran simulasi QRIS, riwayat transaksi UMKM, dan dompet freelancer (F3). */
interface PaymentApiService {
    @GET("projects/{id}/payment")
    suspend fun detail(@Path("id") projectId: String): ApiResponse<PaymentDetailDto>

    @POST("projects/{id}/payment/qris")
    suspend fun regenerateQris(@Path("id") projectId: String): ApiResponse<PaymentDetailDto>

    @POST("projects/{id}/payment/simulate")
    suspend fun simulate(
        @Path("id") projectId: String,
        @Header("Idempotency-Key") idempotencyKey: String,
        @Body body: SimulatePaymentRequest
    ): ApiResponse<PaymentDetailDto>

    @GET("payments/history")
    suspend fun history(
        @Query("group") group: String,
        @Query("search") search: String?,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): ApiResponse<PaymentHistoryDto>

    @GET("wallet")
    suspend fun wallet(): ApiResponse<WalletDto>

    @GET("wallet/transactions")
    suspend fun walletTransactions(
        @Query("type") type: String = "ALL",
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): ApiResponse<PageDto<WalletEntryDto>>
}
