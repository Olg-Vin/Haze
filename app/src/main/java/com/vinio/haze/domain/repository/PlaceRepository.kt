package com.vinio.haze.domain.repository

import com.vinio.haze.domain.model.Place
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {
    fun getAllPlaces() : Flow<List<Place>>
    fun getCities() : Flow<List<String>>
    suspend fun savePlace(place: Place)
    suspend fun saveAllPlaces(places: List<Place>)
    suspend fun exists(id: String): Boolean

    suspend fun getCityById(id: Int): Place?
    suspend fun getPlaceById(id: String): Place
    suspend fun updatePlaceDescription(id: String, description: String)

    fun getOpenedPoiCount(): Flow<Int>
}