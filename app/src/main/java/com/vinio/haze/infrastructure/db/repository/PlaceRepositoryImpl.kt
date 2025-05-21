package com.vinio.haze.infrastructure.db.repository

import com.vinio.haze.diAndUtils.toDomain
import com.vinio.haze.diAndUtils.toEntity
import com.vinio.haze.domain.model.Place
import com.vinio.haze.domain.repository.PlaceRepository
import com.vinio.haze.infrastructure.db.dao.PlaceDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(
    private val placeDao: PlaceDao
) : PlaceRepository {

    override suspend fun getAllPlaces(): Flow<List<Place>> {
        return placeDao.getAll().map { it.toDomain() }
    }

    override suspend fun savePlace(place: Place) {
        val entity = place.toEntity()
        placeDao.insert(entity)
    }

    override suspend fun saveAllPlaces(places: List<Place>) {
        placeDao.insertAll(places.map { it.toEntity() })
    }

    override suspend fun exists(id: String): Boolean {
        return placeDao.exists(id)
    }
}
