package com.vinio.haze.presentation.screens.settingsScreen

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class SettingsPreferences(private val context: Context) {

    companion object {
        val AVATAR_URI_KEY = stringPreferencesKey("avatar_uri")
        val USERNAME_KEY = stringPreferencesKey("username")
        val FOG_OPACITY_KEY = floatPreferencesKey("fog_opacity")
        val SHOW_POI_KEY = booleanPreferencesKey("show_poi")
    }

    val avatarUriFlow: Flow<String?> = context.dataStore.data.map { it[AVATAR_URI_KEY] }
    val usernameFlow: Flow<String?> = context.dataStore.data.map { it[USERNAME_KEY] }
    val fogOpacityFlow: Flow<Float> = context.dataStore.data.map { it[FOG_OPACITY_KEY] ?: 50f }
    val showPOIFlow: Flow<Boolean> = context.dataStore.data.map { it[SHOW_POI_KEY] ?: true }

    suspend fun saveAvatarUri(uri: String) {
        context.dataStore.edit { it[AVATAR_URI_KEY] = uri }
    }

    suspend fun saveUsername(name: String) {
        context.dataStore.edit { it[USERNAME_KEY] = name }
    }

    suspend fun saveFogOpacity(value: Float) {
        context.dataStore.edit { it[FOG_OPACITY_KEY] = value }
    }

    suspend fun saveShowPOI(show: Boolean) {
        context.dataStore.edit { it[SHOW_POI_KEY] = show }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}