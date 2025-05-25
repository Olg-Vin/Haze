package com.vinio.haze.application.useCases

import com.vinio.haze.domain.adapter.MapRequest
import com.vinio.haze.domain.model.Place
import com.vinio.haze.domain.repository.PlaceRepository
import com.yandex.mapkit.GeoObjectCollection.Item
import com.yandex.mapkit.geometry.BoundingBox
import javax.inject.Inject

class FetchPoiUseCase @Inject constructor(
    private val mapRequest: MapRequest,
    private val placeRepository: PlaceRepository,
) {

    suspend fun execute(bbox: BoundingBox): Result<List<Item>> {
        return try {
            val pois = mapRequest.fetchPois(bbox)
            Result.success(pois)
        } catch (e: Exception) {
            Result.failure(e)
        }
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

