package com.vinio.haze.presentation.screens.settingsScreen

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
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
        val USER_LEVEL_KEY = intPreferencesKey("user_level")
        val FOG_COLOR_KEY = intPreferencesKey("fog_color")
        val NOTIFY_ACHIEVEMENTS_KEY = booleanPreferencesKey("notify_achievements")
    }

    val avatarUriFlow: Flow<String?> = context.dataStore.data.map { it[AVATAR_URI_KEY] }
    val usernameFlow: Flow<String?> = context.dataStore.data.map { it[USERNAME_KEY] }
    val fogOpacityFlow: Flow<Float> = context.dataStore.data.map { it[FOG_OPACITY_KEY] ?: 50f }
    val showPOIFlow: Flow<Boolean> = context.dataStore.data.map { it[SHOW_POI_KEY] ?: true }
    val userLevelFlow: Flow<Int> = context.dataStore.data.map { it[USER_LEVEL_KEY] ?: 1 }
    val fogColorFlow: Flow<Color> =
        context.dataStore.data.map { prefs ->
            val colorInt = prefs[FOG_COLOR_KEY] ?: Color(0xFF9575CD).value.toInt()
            Color(colorInt)
        }
    val notifyAchievementsFlow: Flow<Boolean> =
        context.dataStore.data.map { it[NOTIFY_ACHIEVEMENTS_KEY] ?: true }

    suspend fun saveFogColor(color: Color) {
        Log.d("Color", "save color ${color.toArgb()}")
        context.dataStore.edit { it[FOG_COLOR_KEY] = color.toArgb() }
    }

    suspend fun saveNotifyAchievements(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFY_ACHIEVEMENTS_KEY] = enabled }
    }

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

    suspend fun saveUserLevel(level: Int) {
        context.dataStore.edit { it[USER_LEVEL_KEY] = level }
    }
}