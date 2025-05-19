package com.vinio.haze.diAndUtils

import com.vinio.haze.domain.AiRequest
import com.vinio.haze.infrastructure.AiRequestImpl
import com.vinio.haze.presentation.map.YandexMapViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMapViewModel(): YandexMapViewModel {
        return YandexMapViewModel()
    }

    @Provides
    @Singleton
    fun provideAiRequest(): AiRequest {
        return AiRequestImpl()
    }
}

