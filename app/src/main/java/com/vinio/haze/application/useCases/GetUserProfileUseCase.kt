package com.vinio.haze.application.useCases

import com.vinio.haze.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    fun getAvatarUri(): Flow<String?> = settingsRepository.avatarUriFlow
    fun getUserLevel(): Flow<Int> = settingsRepository.userLevelFlow
}
