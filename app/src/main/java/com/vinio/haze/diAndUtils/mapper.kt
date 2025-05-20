package com.vinio.haze.diAndUtils

import com.vinio.haze.domain.model.LocationPoint
import com.vinio.haze.domain.model.Place
import com.vinio.haze.infrastructure.db.entity.LocationPointEntity
import com.vinio.haze.infrastructure.db.entity.PlaceEntity

fun LocationPoint.toEntity() = LocationPointEntity (
    cellLat, cellLon
)

fun LocationPointEntity.toDomain() = LocationPoint (
    cellLat, cellLon
)