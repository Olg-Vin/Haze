package com.vinio.haze.domain.repository

import com.vinio.haze.domain.model.Place
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    suspend fun getAllPlaces() : Flow<List<Place>>
    suspend fun savePlace(place: Place)
    suspend fun saveAllPlaces(places: List<Place>)
    suspend fun exists(id: String): Boolean
}