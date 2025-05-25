package com.vinio.haze.domain.adapter

import com.yandex.mapkit.GeoObjectCollection.Item
import com.yandex.mapkit.geometry.BoundingBox

interface MapRequest {
    suspend fun fetchPois(bbox: BoundingBox): List<Item>
}