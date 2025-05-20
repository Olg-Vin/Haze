package com.vinio.haze.infrastructure.db.repository

import com.vinio.haze.diAndUtils.toDomain
import com.vinio.haze.diAndUtils.toEntity
import com.vinio.haze.domain.model.LocationPoint
import com.vinio.haze.domain.repository.LocationPointRepository
import com.vinio.haze.infrastructure.db.dao.LocationPointDao
import javax.inject.Inject

class LocationPointRepositoryImpl @Inject constructor(
    private val locationPointDao: LocationPointDao
) : LocationPointRepository {
    override suspend fun getAllLocationPoints(): List<LocationPoint> {
        return locationPointDao.getAll().map { it.toDomain() }
    }

    override suspend fun saveLocationPoint(locationPoint: LocationPoint) {
        return locationPointDao.insert(locationPoint.toEntity())
    }
}