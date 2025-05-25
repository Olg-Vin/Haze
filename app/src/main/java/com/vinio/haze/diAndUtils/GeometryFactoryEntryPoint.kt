package com.vinio.haze.diAndUtils

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.locationtech.jts.geom.GeometryFactory

@EntryPoint
@InstallIn(SingletonComponent::class)
interface GeometryFactoryEntryPoint {
    fun geometryFactory(): GeometryFactory
}
