package com.vinio.haze.diAndUtils

import com.vinio.haze.application.useCases.ClearSettingsUseCase
import com.vinio.haze.application.useCases.GetAvatarUriFlowUseCase
import com.vinio.haze.application.useCases.GetFogColorFlowUseCase
import com.vinio.haze.application.useCases.GetFogOpacityFlowUseCase
import com.vinio.haze.application.useCases.GetNotifyAchievementsFlowUseCase
import com.vinio.haze.application.useCases.GetShowPOIFlowUseCase
import com.vinio.haze.application.useCases.GetUserLevelFlowUseCase
import com.vinio.haze.application.useCases.GetUsernameFlowUseCase
import com.vinio.haze.application.useCases.SaveAvatarUriUseCase
import com.vinio.haze.application.useCases.SaveFogColorUseCase
import com.vinio.haze.application.useCases.SaveFogOpacityUseCase
import com.vinio.haze.application.useCases.SaveNotifyAchievementsUseCase
import com.vinio.haze.application.useCases.SaveShowPOIUseCase
import com.vinio.haze.application.useCases.SaveUserLevelUseCase
import com.vinio.haze.application.useCases.SaveUsernameUseCase
import com.vinio.haze.application.useCases.SettingsUseCases
import com.vinio.haze.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsUseCasesModule {

    @Provides
    @Singleton
    fun provideSaveFogColorUseCase(repo: SettingsRepository) = SaveFogColorUseCase(repo)

    @Provides
    @Singleton
    fun provideGetFogColorFlowUseCase(repo: SettingsRepository) = GetFogColorFlowUseCase(repo)

    @Provides
    @Singleton
    fun provideSaveFogOpacityUseCase(repo: SettingsRepository) = SaveFogOpacityUseCase(repo)

    @Provides
    @Singleton
    fun provideGetFogOpacityFlowUseCase(repo: SettingsRepository) = GetFogOpacityFlowUseCase(repo)

    @Provides
    @Singleton
    fun provideSaveAvatarUriUseCase(repo: SettingsRepository) = SaveAvatarUriUseCase(repo)

    @Provides
    @Singleton
    fun provideGetAvatarUriFlowUseCase(repo: SettingsRepository) = GetAvatarUriFlowUseCase(repo)

    @Provides
    @Singleton
    fun provideSaveUsernameUseCase(repo: SettingsRepository) = SaveUsernameUseCase(repo)

    @Provides
    @Singleton
    fun provideGetUsernameFlowUseCase(repo: SettingsRepository) = GetUsernameFlowUseCase(repo)

    @Provides
    @Singleton
    fun provideSaveShowPOIUseCase(repo: SettingsRepository) = SaveShowPOIUseCase(repo)

    @Provides
    @Singleton
    fun provideGetShowPOIFlowUseCase(repo: SettingsRepository) = GetShowPOIFlowUseCase(repo)

    @Provides
    @Singleton
    fun provideSaveUserLevelUseCase(repo: SettingsRepository) = SaveUserLevelUseCase(repo)

    @Provides
    @Singleton
    fun provideGetUserLevelFlowUseCase(repo: SettingsRepository) = GetUserLevelFlowUseCase(repo)

    @Provides
    @Singleton
    fun provideSaveNotifyAchievementsUseCase(repo: SettingsRepository) = SaveNotifyAchievementsUseCase(repo)

    @Provides
    @Singleton
    fun provideGetNotifyAchievementsFlowUseCase(repo: SettingsRepository) = GetNotifyAchievementsFlowUseCase(repo)

    @Provides
    @Singleton
    fun provideClearSettingsUseCase(repo: SettingsRepository) = ClearSettingsUseCase(repo)

    @Provides
    @Singleton
    fun provideSettingsUseCases(
        saveFogColor: SaveFogColorUseCase,
        getFogColorFlow: GetFogColorFlowUseCase,
        saveFogOpacity: SaveFogOpacityUseCase,
        getFogOpacityFlow: GetFogOpacityFlowUseCase,
        saveAvatarUri: SaveAvatarUriUseCase,
        getAvatarUriFlow: GetAvatarUriFlowUseCase,
        saveUsername: SaveUsernameUseCase,
        getUsernameFlow: GetUsernameFlowUseCase,
        saveShowPOI: SaveShowPOIUseCase,
        getShowPOIFlow: GetShowPOIFlowUseCase,
        saveUserLevel: SaveUserLevelUseCase,
        getUserLevelFlow: GetUserLevelFlowUseCase,
        saveNotifyAchievements: SaveNotifyAchievementsUseCase,
        getNotifyAchievementsFlow: GetNotifyAchievementsFlowUseCase,
        clearSettings: ClearSettingsUseCase,
    ) = SettingsUseCases(
        saveFogColor = saveFogColor,
        getFogColorFlow = getFogColorFlow,
        saveFogOpacity = saveFogOpacity,
        getFogOpacityFlow = getFogOpacityFlow,
        saveAvatarUri = saveAvatarUri,
        getAvatarUriFlow = getAvatarUriFlow,
        saveUsername = saveUsername,
        getUsernameFlow = getUsernameFlow,
        saveShowPOI = saveShowPOI,
        getShowPOIFlow = getShowPOIFlow,
        saveUserLevel = saveUserLevel,
        getUserLevelFlow = getUserLevelFlow,
        saveNotifyAchievements = saveNotifyAchievements,
        getNotifyAchievementsFlow = getNotifyAchievementsFlow,
        clearSettings = clearSettings
    )
}
