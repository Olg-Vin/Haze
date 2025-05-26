package com.vinio.haze.application.useCases

import androidx.compose.ui.graphics.Color
import com.vinio.haze.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

data class SettingsUseCases(
    val saveFogColor: SaveFogColorUseCase,
    val getFogColorFlow: GetFogColorFlowUseCase,
    val saveFogOpacity: SaveFogOpacityUseCase,
    val getFogOpacityFlow: GetFogOpacityFlowUseCase,
    val saveAvatarUri: SaveAvatarUriUseCase,
    val getAvatarUriFlow: GetAvatarUriFlowUseCase,
    val saveUsername: SaveUsernameUseCase,
    val getUsernameFlow: GetUsernameFlowUseCase,
    val saveShowPOI: SaveShowPOIUseCase,
    val getShowPOIFlow: GetShowPOIFlowUseCase,
    val saveUserLevel: SaveUserLevelUseCase,
    val getUserLevelFlow: GetUserLevelFlowUseCase,
    val saveNotifyAchievements: SaveNotifyAchievementsUseCase,
    val getNotifyAchievementsFlow: GetNotifyAchievementsFlowUseCase,
    val clearSettings: ClearSettingsUseCase,
)

class SaveFogColorUseCase(private val repo: SettingsRepository) {
    suspend operator fun invoke(color: Color) = repo.saveFogColor(color)
}

class GetFogColorFlowUseCase(private val repo: SettingsRepository) {
    operator fun invoke(): Flow<Color> = repo.fogColorFlow
}

class SaveFogOpacityUseCase(private val repo: SettingsRepository) {
    suspend operator fun invoke(value: Float) = repo.saveFogOpacity(value)
}

class GetFogOpacityFlowUseCase(private val repo: SettingsRepository) {
    operator fun invoke(): Flow<Float> = repo.fogOpacityFlow
}

class SaveAvatarUriUseCase(private val repo: SettingsRepository) {
    suspend operator fun invoke(uri: String) = repo.saveAvatarUri(uri)
}

class GetAvatarUriFlowUseCase(private val repo: SettingsRepository) {
    operator fun invoke(): Flow<String?> = repo.avatarUriFlow
}

class SaveUsernameUseCase(private val repo: SettingsRepository) {
    suspend operator fun invoke(name: String) = repo.saveUsername(name)
}

class GetUsernameFlowUseCase(private val repo: SettingsRepository) {
    operator fun invoke(): Flow<String?> = repo.usernameFlow
}

class SaveShowPOIUseCase(private val repo: SettingsRepository) {
    suspend operator fun invoke(show: Boolean) = repo.saveShowPOI(show)
}

class GetShowPOIFlowUseCase(private val repo: SettingsRepository) {
    operator fun invoke(): Flow<Boolean> = repo.showPOIFlow
}

class SaveUserLevelUseCase(private val repo: SettingsRepository) {
    suspend operator fun invoke(level: Int) = repo.saveUserLevel(level)
}

class GetUserLevelFlowUseCase(private val repo: SettingsRepository) {
    operator fun invoke(): Flow<Int> = repo.userLevelFlow
}

class SaveNotifyAchievementsUseCase(private val repo: SettingsRepository) {
    suspend operator fun invoke(enabled: Boolean) = repo.saveNotifyAchievements(enabled)
}

class GetNotifyAchievementsFlowUseCase(private val repo: SettingsRepository) {
    operator fun invoke(): Flow<Boolean> = repo.notifyAchievementsFlow
}

class ClearSettingsUseCase(private val repo: SettingsRepository) {
    suspend operator fun invoke() = repo.clearAll()
}
