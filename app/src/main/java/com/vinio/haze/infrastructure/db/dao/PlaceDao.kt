package com.vinio.haze.infrastructure.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vinio.haze.infrastructure.db.entity.PlaceEntity

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places")
    suspend fun getAll(): List<PlaceEntity>

    @Query("SELECT DISTINCT city FROM places WHERE city IS NOT NULL AND city != ''")
    suspend fun getDistinctCities(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<PlaceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(place: PlaceEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM places WHERE id = :id)")
    suspend fun exists(id: String): Boolean
}