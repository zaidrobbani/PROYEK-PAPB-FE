package com.example.proyek_papb_fe.core.session

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AccountRole { UMKM, FREELANCER, ADMIN;
    companion object {
        fun from(value: String?): AccountRole? = entries.firstOrNull { it.name == value }
    }
}

data class SessionUser(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val role: AccountRole,
    val profileImageUrl: String?
)

data class UserSession(
    val accessToken: String,
    val expiresAtMillis: Long?,
    val user: SessionUser
)

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _session = MutableStateFlow(load())
    val session: StateFlow<UserSession?> = _session.asStateFlow()

    private val _expiredEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val expiredEvents: SharedFlow<Unit> = _expiredEvents.asSharedFlow()

    val accessToken: String? get() = _session.value?.accessToken
    val currentUser: SessionUser? get() = _session.value?.user

    fun isTokenStillValid(nowMillis: Long = System.currentTimeMillis()): Boolean {
        val current = _session.value ?: return false
        return current.expiresAtMillis == null || current.expiresAtMillis > nowMillis
    }

    fun save(session: UserSession) {
        prefs.edit {
            putString(KEY_TOKEN, session.accessToken)
            putLong(KEY_EXPIRES_AT, session.expiresAtMillis ?: -1L)
            putUser(session.user)
        }
        _session.value = session
    }

    fun updateUser(user: SessionUser) {
        val current = _session.value ?: return
        prefs.edit { putUser(user) }
        _session.value = current.copy(user = user)
    }

    fun clear() {
        prefs.edit { clear() }
        _session.value = null
    }

    fun expire(rejectedToken: String) {
        if (_session.value?.accessToken != rejectedToken) return
        clear()
        _expiredEvents.tryEmit(Unit)
    }

    private fun android.content.SharedPreferences.Editor.putUser(user: SessionUser) {
        putString(KEY_USER_ID, user.id)
        putString(KEY_NAME, user.name)
        putString(KEY_EMAIL, user.email)
        putString(KEY_PHONE, user.phoneNumber)
        putString(KEY_ROLE, user.role.name)
        putString(KEY_PHOTO, user.profileImageUrl)
    }

    private fun load(): UserSession? {
        val token = prefs.getString(KEY_TOKEN, null) ?: return null
        val role = AccountRole.from(prefs.getString(KEY_ROLE, null)) ?: return null
        val user = SessionUser(
            id = prefs.getString(KEY_USER_ID, null) ?: return null,
            name = prefs.getString(KEY_NAME, null).orEmpty(),
            email = prefs.getString(KEY_EMAIL, null).orEmpty(),
            phoneNumber = prefs.getString(KEY_PHONE, null),
            role = role,
            profileImageUrl = prefs.getString(KEY_PHOTO, null)
        )
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, -1L).takeIf { it > 0 }
        return UserSession(token, expiresAt, user)
    }

    private companion object {
        const val PREFS_NAME = "temukarya_session"
        const val KEY_TOKEN = "access_token"
        const val KEY_EXPIRES_AT = "expires_at"
        const val KEY_USER_ID = "user_id"
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
        const val KEY_PHONE = "phone_number"
        const val KEY_ROLE = "role"
        const val KEY_PHOTO = "profile_image_url"
    }
}
