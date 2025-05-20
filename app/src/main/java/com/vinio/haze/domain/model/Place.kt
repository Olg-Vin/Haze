package com.vinio.haze.domain.model

data class Place(
    val name: String,
    val address: String?,
    val description: String?,
    val lat: Double,
    val lon: Double,
)