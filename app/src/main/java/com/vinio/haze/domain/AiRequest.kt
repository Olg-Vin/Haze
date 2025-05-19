package com.vinio.haze.domain

interface AiRequest {
    suspend fun getPoiDescription(name: String, lan: Double, lon: Double) : String
    suspend fun getPoiFact(name: String, description: String, lan: Double, lon: Double) : String
}