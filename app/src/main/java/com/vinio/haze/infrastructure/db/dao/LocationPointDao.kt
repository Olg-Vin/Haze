package com.vinio.haze.infrastructure.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vinio.haze.infrastructure.db.entity.LocationPointEntity

@Dao
interface LocationPointDao {
    @Query("SELECT * FROM location_point")
    suspend fun getAll(): List<LocationPointEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locationPoints: List<LocationPointEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(locationPoint: LocationPointEntity)
}