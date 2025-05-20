package com.vinio.haze.domain.location

import android.location.Location
import android.util.Log
import com.vinio.haze.domain.model.LocationPoint
import com.vinio.haze.domain.repository.LocationPointRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class LocationRepository @Inject constructor(
    val locationPointRepository: LocationPointRepository
) {
    private val _locationFlow = MutableStateFlow<Location?>(null)
    val locationFlow: StateFlow<Location?> = _locationFlow

    suspend fun updateLocation(location: Location) {
        Log.d("LocationRepository", "new location: ${location.latitude}, ${location.longitude}")
        locationPointRepository.saveLocationPoint(LocationPoint(location.latitude, location.longitude))
        _locationFlow.value = location
    }
}

