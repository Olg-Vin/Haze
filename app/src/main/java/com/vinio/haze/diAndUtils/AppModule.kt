package com.vinio.haze.diAndUtils

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.LocationServices
import com.vinio.haze.domain.ai.AiRequest
import com.vinio.haze.domain.location.LocationClient
import com.vinio.haze.domain.location.LocationRepository
import com.vinio.haze.domain.repository.LocationPointRepository
import com.vinio.haze.domain.repository.PlaceRepository
import com.vinio.haze.infrastructure.ai.AiRequestImpl
import com.vinio.haze.infrastructure.db.AppDatabase
import com.vinio.haze.infrastructure.db.dao.LocationPointDao
import com.vinio.haze.infrastructure.db.dao.PlaceDao
import com.vinio.haze.infrastructure.db.repository.LocationPointRepositoryImpl
import com.vinio.haze.infrastructure.db.repository.PlaceRepositoryImpl
import com.vinio.haze.infrastructure.location.DefaultLocationClient
import com.vinio.haze.presentation.screens.settingsScreen.SettingsPreferences
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
    ): LocationClient {
        return DefaultLocationClient(
            context,
            fusedLocationClient
        )
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        locationPointRepository: LocationPointRepository
    ): LocationRepository {
        return LocationRepository(locationPointRepository)
    }

    @Provides
    @Singleton
    fun provideLocationPointRepository(
        locationPointDao: LocationPointDao
    ) : LocationPointRepository {
        return LocationPointRepositoryImpl(locationPointDao)
    }

    @Provides
    @Singleton
    fun providePlaceRepository(
        placeDao: PlaceDao
    ) : PlaceRepository {
        return PlaceRepositoryImpl(placeDao)
    }

    @Provides
    @Singleton
    fun providePlaceDao(
        appDatabase: AppDatabase
    ): PlaceDao {
        return appDatabase.placeDao()
    }

    @Provides
    @Singleton
    fun provideLocationDao(
        appDatabase: AppDatabase
    ): LocationPointDao {
        return appDatabase.locationDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "places.db")
            .build()
    }

    @Provides
    @Singleton
    fun provideSettingsPreferences(
        @ApplicationContext context: Context
    ): SettingsPreferences {
        return SettingsPreferences(context)
    }
}

