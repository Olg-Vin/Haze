package com.vinio.haze.diAndUtils

import android.content.Context
import androidx.room.Room
import com.vinio.haze.infrastructure.db.AppDatabase
import com.vinio.haze.infrastructure.db.dao.LocationPointDao
import com.vinio.haze.infrastructure.db.dao.PlaceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {
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
}