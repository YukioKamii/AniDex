package com.example.anidex.data.local.session

import android.content.Context

class UserSessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("anidex_session", Context.MODE_PRIVATE)

    fun saveCurrentUserId(userId: Int) {
        prefs.edit().putInt(KEY_CURRENT_USER_ID, userId).apply()
    }

    fun getCurrentUserId(): Int? {
        val value = prefs.getInt(KEY_CURRENT_USER_ID, -1)
        return if (value == -1) null else value
    }

    fun isLoggedIn(): Boolean {
        return getCurrentUserId() != null
    }

    fun logout() {
        prefs.edit().remove(KEY_CURRENT_USER_ID).apply()
    }

    companion object {
        private const val KEY_CURRENT_USER_ID = "current_user_id"
    }
}