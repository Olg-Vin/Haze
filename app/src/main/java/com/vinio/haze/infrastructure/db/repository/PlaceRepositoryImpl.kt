package com.vinio.haze.infrastructure.db.repository

import com.vinio.haze.domain.model.Place
import com.vinio.haze.domain.repository.PlaceRepository

class PlaceRepositoryImpl : PlaceRepository {
    override suspend fun getAllPlaces(): List<Place> {
        TODO("Not yet implemented")
    }

    override suspend fun savePlace(place: Place) {
        TODO("Not yet implemented")
    }

    override suspend fun saveAllPlaces(places: List<Place>) {
        TODO("Not yet implemented")
    }
}