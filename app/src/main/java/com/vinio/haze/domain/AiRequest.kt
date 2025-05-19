package com.vinio.haze.domain

import kotlinx.coroutines.flow.Flow

interface AiRequest {
    suspend fun getPoiDescription(name: String, lan: Double, lon: Double) : String
    suspend fun getPoiFact(name: String, description: String, lan: Double, lon: Double) : String
    fun streamPoiDescription(name: String, lat: Double, lon: Double): Flow<String>
}