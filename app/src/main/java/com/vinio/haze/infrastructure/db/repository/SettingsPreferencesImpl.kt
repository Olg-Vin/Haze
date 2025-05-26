package com.vinio.haze.infrastructure.db.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vinio.haze.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import javax.inject.Inject

class SettingsPreferencesImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object Keys {
        val FOG_COLOR = intPreferencesKey("fog_color")
        val FOG_OPACITY = floatPreferencesKey("fog_opacity")
        val AVATAR_URI = stringPreferencesKey("avatar_uri")
        val USERNAME = stringPreferencesKey("username")
        val SHOW_POI = booleanPreferencesKey("show_poi")
        val USER_LEVEL = intPreferencesKey("user_level")
        val NOTIFY_ACHIEVEMENTS = booleanPreferencesKey("notify_achievements")
    }

    override val fogColorFlow: Flow<Color> = dataStore.data.map { prefs ->
        val colorInt = prefs[Keys.FOG_COLOR] ?: Color(0xFF9575CD).toArgb()
        Color(colorInt)
    }

    override suspend fun saveFogColor(color: Color) {
        dataStore.edit { it[Keys.FOG_COLOR] = color.toArgb() }
    }

    override val fogOpacityFlow: Flow<Float> = dataStore.data.map {
        it[Keys.FOG_OPACITY] ?: 50f
    }

    override suspend fun saveFogOpacity(value: Float) {
        dataStore.edit { it[Keys.FOG_OPACITY] = value }
    }

    override val avatarUriFlow: Flow<String?> = dataStore.data.map {
        it[Keys.AVATAR_URI]
    }

    override suspend fun saveAvatarUri(uri: String) {
        dataStore.edit { it[Keys.AVATAR_URI] = uri }
    }

    override val usernameFlow: Flow<String?> = dataStore.data.map {
        it[Keys.USERNAME]
    }

    override suspend fun saveUsername(name: String) {
        dataStore.edit { it[Keys.USERNAME] = name }
    }

    override val showPOIFlow: Flow<Boolean> = dataStore.data.map {
        it[Keys.SHOW_POI] ?: true
    }

    override suspend fun saveShowPOI(show: Boolean) {
        dataStore.edit { it[Keys.SHOW_POI] = show }
    }

    override val userLevelFlow: Flow<Int> = dataStore.data.map {
        it[Keys.USER_LEVEL] ?: 1
    }

    override suspend fun saveUserLevel(level: Int) {
        dataStore.edit { it[Keys.USER_LEVEL] = level }
    }

    override val notifyAchievementsFlow: Flow<Boolean> = dataStore.data.map {
        it[Keys.NOTIFY_ACHIEVEMENTS] ?: true
    }

    override suspend fun saveNotifyAchievements(enabled: Boolean) {
        dataStore.edit { it[Keys.NOTIFY_ACHIEVEMENTS] = enabled }
    }

    override suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
