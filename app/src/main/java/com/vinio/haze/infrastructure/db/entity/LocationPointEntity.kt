package com.vinio.haze.infrastructure.db.entity

import androidx.room.Entity

@Entity(tableName = "location_point", primaryKeys = ["cellLat", "cellLon"])
data class LocationPointEntity (
    val cellLat: Double,
    val cellLon: Double,
    val openedAt: Long = System.currentTimeMillis(),
)