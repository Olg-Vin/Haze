package com.vinio.haze.application.useCases

import android.content.Context
import android.location.Geocoder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class GetCityByLocationUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun getCityName(lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val address = geocoder.getFromLocation(lat, lon, 1)?.firstOrNull()
            address?.locality // или address?.subAdminArea, если нужна альтернатива
        } catch (e: Exception) {
            null
        }
    }
}