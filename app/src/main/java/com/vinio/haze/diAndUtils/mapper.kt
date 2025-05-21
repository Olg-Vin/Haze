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

fun PlaceEntity.toDomain(): Place = Place(
    id = id,
    name = name,
    city = city ?: "",
    address= address ?: "",
    description = description,
    lat = lat,
    lon = lon
)

fun Place.toEntity(): PlaceEntity = PlaceEntity(
    id = id ?: "",
    name = name,
    city = city,
    address = address,
    description = description,
    lat = lat,
    lon = lon
)