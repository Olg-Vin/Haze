package com.vinio.haze.domain.model

data class Place(
    val id: String?,
    val name: String,
    val city: String?,
    val address: String?,
    val description: String?,
    val lat: Double,
    val lon: Double,
)