package com.vinio.haze.infrastructure.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity (
    @PrimaryKey val id: String,
    val name: String,
    val city: String?,
    val address: String?,
    val description: String?,
    val lat: Double,
    val lon: Double,
    val fetchAt: Long = System.currentTimeMillis(),
)