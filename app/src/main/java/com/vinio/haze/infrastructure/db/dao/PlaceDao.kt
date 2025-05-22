package com.vinio.haze.infrastructure.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vinio.haze.infrastructure.db.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places")
    fun getAll(): Flow<List<PlaceEntity>>

    @Query("SELECT DISTINCT city FROM places WHERE city IS NOT NULL AND city != ''")
    fun getDistinctCities(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<PlaceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(place: PlaceEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM places WHERE id = :id)")
    suspend fun exists(id: String): Boolean

    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getById(id: String): PlaceEntity?

    @Query("UPDATE places SET description = :description WHERE id = :id")
    suspend fun updateDescription(id: String, description: String)

    @Query("SELECT COUNT(*) FROM places")
    fun getOpenedPoiCount(): Flow<Int>
}