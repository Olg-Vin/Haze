package com.vinio.haze.application.useCases

import android.util.Log
import com.vinio.haze.domain.adapter.MapRequest
import com.vinio.haze.domain.exceptions.AppError
import com.vinio.haze.domain.model.Place
import com.vinio.haze.domain.repository.PlaceRepository
import com.yandex.mapkit.GeoObjectCollection.Item
import com.yandex.mapkit.geometry.BoundingBox
import kotlinx.io.IOException
import javax.inject.Inject

class FetchPoiUseCase @Inject constructor(
    private val mapRequest: MapRequest,
    private val placeRepository: PlaceRepository,
) {

    suspend fun execute(bbox: BoundingBox): Result<List<Item>> {
        return mapRequest.fetchPois(bbox)
    }

    suspend fun savePlaceIfNotExists(place: Place) {
        val id = generatePlaceId(place.name, place.lat, place.lon)
        val exists = placeRepository.exists(id)
        if (!exists) {
            placeRepository.savePlace(place.copy(id = id))
        }
    }

    private fun generatePlaceId(name: String, lat: Double, lon: Double): String {
        return "${name}_${lat}_${lon}".hashCode().toString()
    }
}

