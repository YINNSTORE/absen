package com.albasroh.absensi.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.store by preferencesDataStore(name = "prefs")

class Prefs(private val context: Context) {
    private val userIdKey = longPreferencesKey("uid")
    private val roleKey = stringPreferencesKey("role")
    private val themeKey = stringPreferencesKey("theme")

    val session = context.store.data.map { preferences ->
        preferences[userIdKey] to preferences[roleKey]
    }

    val themeFlow = context.store.data.map { preferences ->
        preferences[themeKey] ?: "SYSTEM"
    }

    suspend fun login(userId: Long, role: String) {
        context.store.edit { preferences ->
            preferences[userIdKey] = userId
            preferences[roleKey] = role
        }
    }

    // Logout HANYA menghapus sesi. Preferensi tema tetap disimpan.
    suspend fun logout() {
        context.store.edit { preferences ->
            preferences.remove(userIdKey)
            preferences.remove(roleKey)
        }
    }

    suspend fun theme(value: String) {
        context.store.edit { preferences ->
            preferences[themeKey] = value
        }
    }
}
