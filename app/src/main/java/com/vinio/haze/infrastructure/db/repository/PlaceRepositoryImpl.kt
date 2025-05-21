package com.vinio.haze.infrastructure.db.repository

import com.vinio.haze.diAndUtils.toDomain
import com.vinio.haze.diAndUtils.toEntity
import com.vinio.haze.domain.model.Place
import com.vinio.haze.domain.repository.PlaceRepository
import com.vinio.haze.infrastructure.db.dao.PlaceDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(
    private val placeDao: PlaceDao
) : PlaceRepository {

    override fun getAllPlaces(): Flow<List<Place>> {
        return placeDao.getAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getCities(): Flow<List<String>> {
        return placeDao.getDistinctCities()
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

    override suspend fun getCityById(id: Int): Place? {
        TODO("Not yet implemented")
    }

    override suspend fun getPlaceById(id: String): Place {
        return placeDao.getById(id)?.toDomain()
            ?: throw IllegalArgumentException("Place not found with id: $id")
    }

    override suspend fun updatePlaceDescription(id: String, description: String) {
        placeDao.updateDescription(id, description)
    }
}
