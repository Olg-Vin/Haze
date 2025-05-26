package com.vinio.haze.domain.repository

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val fogColorFlow: Flow<Color>
    suspend fun saveFogColor(color: Color)

    val fogOpacityFlow: Flow<Float>
    suspend fun saveFogOpacity(value: Float)

    val avatarUriFlow: Flow<String?>
    suspend fun saveAvatarUri(uri: String)

    val usernameFlow: Flow<String?>
    suspend fun saveUsername(name: String)

    val showPOIFlow: Flow<Boolean>
    suspend fun saveShowPOI(show: Boolean)

    val userLevelFlow: Flow<Int>
    suspend fun saveUserLevel(level: Int)

    val notifyAchievementsFlow: Flow<Boolean>
    suspend fun saveNotifyAchievements(enabled: Boolean)

    suspend fun clearAll()
}

