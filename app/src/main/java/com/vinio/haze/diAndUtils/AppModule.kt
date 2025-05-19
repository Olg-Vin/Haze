package com.vinio.haze.diAndUtils

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.vinio.haze.domain.AiRequest
import com.vinio.haze.domain.LocationClient
import com.vinio.haze.domain.LocationRepository
import com.vinio.haze.infrastructure.AiRequestImpl
import com.vinio.haze.infrastructure.DefaultLocationClient
import com.vinio.haze.presentation.map.YandexMapViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAiRequest(): AiRequest {
        return AiRequestImpl()
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ) = LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @Singleton
    fun provideLocationClient(
        @ApplicationContext context: Context,
        fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient
    ): LocationClient = DefaultLocationClient(context, fusedLocationClient)

    @Provides
    @Singleton
    fun provideLocationRepository(): LocationRepository = LocationRepository()
}

