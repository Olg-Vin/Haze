package com.vinio.haze.domain

import android.location.Location
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class LocationRepository @Inject constructor() {
    private val _locationFlow = MutableStateFlow<Location?>(null)
    val locationFlow: StateFlow<Location?> = _locationFlow

    fun updateLocation(location: Location) {
        Log.d("LocationRepository", "new location: ${location.latitude}, ${location.longitude}")
        _locationFlow.value = location
    }
}

