package com.vinio.haze.infrastructure.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vinio.haze.infrastructure.db.dao.LocationPointDao
import com.vinio.haze.infrastructure.db.dao.PlaceDao
import com.vinio.haze.infrastructure.db.entity.LocationPointEntity
import com.vinio.haze.infrastructure.db.entity.PlaceEntity

@Database(
    entities = [PlaceEntity::class, LocationPointEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao
    abstract fun locationDao(): LocationPointDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "places.db" // Имя базы данных
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}