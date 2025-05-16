package com.vinio.haze.diAndUtils

import com.vinio.haze.presentation.map.YandexMapViewModel
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
}

