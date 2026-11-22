package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.data.remote.dto.toDomain
import com.example.proyek_papb_fe.core.data.source.PaymentRemoteDataSource
import com.example.proyek_papb_fe.core.domain.model.PaymentDetail
import com.example.proyek_papb_fe.core.domain.model.PaymentHistory
import com.example.proyek_papb_fe.core.domain.model.TransactionGroup
import com.example.proyek_papb_fe.core.domain.model.Wallet
import com.example.proyek_papb_fe.core.domain.model.WalletEntry

/** F3: pembayaran simulasi (tanpa uang sungguhan). Nominal selalu dihitung BE dari kesepakatan. */
interface PaymentRepository {
    suspend fun payment(projectId: String): PaymentDetail
    suspend fun regenerateQris(projectId: String): PaymentDetail

    /** [idempotencyKey] sama untuk klik ganda pada satu percobaan bayar, agar tidak tercatat dua kali. */
    suspend fun simulatePay(projectId: String, idempotencyKey: String, success: Boolean = true): PaymentDetail

    suspend fun history(group: TransactionGroup, search: String? = null): PaymentHistory
    suspend fun wallet(): Wallet

    /** [type]: ALL, HELD, atau INCOME. */
    suspend fun walletEntries(type: String = "ALL"): List<WalletEntry>
}

class DefaultPaymentRepository(
    private val remote: PaymentRemoteDataSource
) : PaymentRepository {

    override suspend fun payment(projectId: String): PaymentDetail = remote.detail(projectId).toDomain()

    override suspend fun regenerateQris(projectId: String): PaymentDetail = remote.regenerateQris(projectId).toDomain()

    override suspend fun simulatePay(projectId: String, idempotencyKey: String, success: Boolean): PaymentDetail =
        remote.simulate(projectId, idempotencyKey, if (success) "SUCCESS" else "FAILED").toDomain()

    override suspend fun history(group: TransactionGroup, search: String?): PaymentHistory {
        val dto = remote.history(group.name, search?.trim()?.ifBlank { null })
        return PaymentHistory(dto.items.orEmpty().map { it.toDomain() }, dto.counts.orEmpty())
    }

    override suspend fun wallet(): Wallet = remote.wallet().toDomain()

    override suspend fun walletEntries(type: String): List<WalletEntry> =
        remote.walletTransactions(type).items.orEmpty().map { it.toDomain() }
}
