package com.vinio.haze.diAndUtils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.android.gms.location.LocationServices
import com.vinio.haze.domain.adapter.AiRequest
import com.vinio.haze.domain.adapter.MapRequest
import com.vinio.haze.domain.location.LocationClient
import com.vinio.haze.domain.location.LocationRepository
import com.vinio.haze.domain.repository.LocationPointRepository
import com.vinio.haze.domain.repository.PlaceRepository
import com.vinio.haze.domain.repository.SettingsRepository
import com.vinio.haze.infrastructure.adapter.AiRequestImpl
import com.vinio.haze.infrastructure.adapter.MapRequestImpl
import com.vinio.haze.infrastructure.db.dao.LocationPointDao
import com.vinio.haze.infrastructure.db.dao.PlaceDao
import com.vinio.haze.infrastructure.db.repository.LocationPointRepositoryImpl
import com.vinio.haze.infrastructure.db.repository.PlaceRepositoryImpl
import com.vinio.haze.infrastructure.db.repository.SettingsPreferencesImpl
import com.vinio.haze.infrastructure.location.DefaultLocationClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.locationtech.jts.geom.GeometryFactory
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
    ): LocationPointRepository {
        return LocationPointRepositoryImpl(locationPointDao)
    }

    @Provides
    @Singleton
    fun providePlaceRepository(
        placeDao: PlaceDao
    ): PlaceRepository {
        return PlaceRepositoryImpl(placeDao)
    }

    @Provides
    @Singleton
    fun provideMapRequest(
    ): MapRequest {
        return MapRequestImpl()
    }

    @Provides
    @Singleton
    fun provideGeometryFactory(): GeometryFactory {
        return GeometryFactory()
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataStore: DataStore<Preferences>
    ): SettingsRepository {
        return SettingsPreferencesImpl(dataStore)
    }
}