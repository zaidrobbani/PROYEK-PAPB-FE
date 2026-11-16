package com.example.proyek_papb_fe.feature.auth.login.container

import android.util.Patterns
import com.example.proyek_papb_fe.feature.auth.role_selection.container.UserRole

internal data class LoginErrors(
    val email: String? = null,
    val password: String? = null
) {
    val hasError: Boolean
        get() = email != null || password != null
}

/** Pesan format email diambil dari lapisan error tersembunyi pada frame Figma Login. */
internal fun validateLogin(email: String, password: String) = LoginErrors(
    email = when {
        email.isBlank() -> "Alamat email wajib diisi."
        !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
            "Format email tidak valid (harus mengandung domain lengkap)"
        else -> null
    },
    password = "Kata sandi wajib diisi.".takeIf { password.isEmpty() }
)

internal const val LoginFailedMessage = "Email atau kata sandi salah."

/**
 * Akun contoh dari data sampel frame Figma, hanya untuk uji UI.
 * Bukan autentikasi sungguhan; tidak ada data yang dikirim atau disimpan.
 */
private data class DummyAccount(val email: String, val password: String, val role: UserRole)

private val DummyAccounts = mutableListOf(
    DummyAccount("rina.pratiwi@kulinerbandung.id", "RahasiaKarya2024!", UserRole.Umkm),
    DummyAccount("andi.ramadhan@email.com", "KaryaHebat2024", UserRole.Freelancer)
)

internal enum class ChangePasswordResult { Changed, WrongCurrent, AccountNotFound }

/**
 * Keamanan & Kata Sandi: mengganti kata sandi akun dummy di memori (simulasi lokal, hilang saat
 * aplikasi ditutup). Bukan penyimpanan sungguhan; Blueprint TK-NF meminta hashing di server.
 */
internal fun changeDummyPassword(email: String, current: String, new: String): ChangePasswordResult {
    val index = DummyAccounts.indexOfFirst { it.email.equals(email.trim(), ignoreCase = true) }
    if (index < 0) return ChangePasswordResult.AccountNotFound
    if (DummyAccounts[index].password != current) return ChangePasswordResult.WrongCurrent
    DummyAccounts[index] = DummyAccounts[index].copy(password = new)
    return ChangePasswordResult.Changed
}

/** Mengembalikan peran akun bila cocok, atau null bila email/kata sandi salah. */
internal fun authenticateDummy(email: String, password: String): UserRole? =
    DummyAccounts.firstOrNull {
        it.email.equals(email.trim(), ignoreCase = true) && it.password == password
    }?.role
