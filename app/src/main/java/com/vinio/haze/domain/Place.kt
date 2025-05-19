package com.vinio.haze.domain

data class Place(
    val name: String,
    val description: String?,
    val lat: Double,
    val lon: Double,
)