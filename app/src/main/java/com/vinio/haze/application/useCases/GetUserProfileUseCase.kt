package com.vinio.haze.application.useCases

import com.vinio.haze.presentation.screens.settingsScreen.SettingsPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val settingsPreferences: SettingsPreferences
) {
    fun getAvatarUri(): Flow<String?> = settingsPreferences.avatarUriFlow
    fun getUserLevel(): Flow<Int> = settingsPreferences.userLevelFlow
}
