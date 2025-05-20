package com.vinio.haze.domain.repository

import com.vinio.haze.domain.model.Place

interface PlaceRepository {
    suspend fun getAllPlaces() : List<Place>
    suspend fun savePlace(place: Place)
    suspend fun saveAllPlaces(places: List<Place>)
}