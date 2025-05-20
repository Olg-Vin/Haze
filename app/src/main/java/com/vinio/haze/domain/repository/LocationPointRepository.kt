package com.vinio.haze.domain.repository

import com.vinio.haze.domain.model.LocationPoint

interface LocationPointRepository {
    suspend fun getAllLocationPoints() : List<LocationPoint>
    suspend fun saveLocationPoint(locationPoint: LocationPoint)
}