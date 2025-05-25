package com.vinio.haze.application.useCases

import com.vinio.haze.domain.model.Place
import com.vinio.haze.domain.repository.PlaceRepository
import com.yandex.mapkit.GeoObjectCollection.Item
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.BusinessObjectMetadata
import com.yandex.mapkit.search.ToponymObjectMetadata
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import javax.inject.Inject

class ProcessPoiItemUseCase @Inject constructor(
    private val placeRepository: PlaceRepository,
    private val geometryFactory: GeometryFactory,
) {

    suspend fun execute(item: Item, visibleRings: List<LinearRing>): Place? {
        val geometryPoint = item.obj?.geometry?.firstOrNull()?.point ?: return null
        val point = Point(geometryPoint.latitude, geometryPoint.longitude)

        // Проверка: находится ли точка внутри хотя бы одного LinearRing (создаём Polygon на лету)
        val jtsPoint = geometryFactory.createPoint(Coordinate(point.longitude, point.latitude))

        val isInVisibleArea = visibleRings.any { ring ->
            val polygon = toJtsPolygon(ring.points) // это JTS Polygon
            polygon.contains(jtsPoint) // всё JTS, всё совместимо
        }

        if (!isInVisibleArea) return null

        val name = item.obj?.name?.toString().orEmpty()

        val metadata = item.obj?.metadataContainer
        val toponymMetadata = metadata?.getItem(ToponymObjectMetadata::class.java)
        val toponymAddress = toponymMetadata?.address?.formattedAddress
        val business = metadata?.getItem(BusinessObjectMetadata::class.java)
        val businessAddress = business?.address?.formattedAddress
        val address = businessAddress ?: toponymAddress

        val cityFromToponym = toponymMetadata?.address?.components
            ?.firstOrNull { it.kinds.contains(Address.Component.Kind.LOCALITY) }
            ?.name

        val cityFromBusiness = business?.address?.components
            ?.firstOrNull { component ->
                component.kinds.any { kind ->
                    kind == Address.Component.Kind.LOCALITY || kind == Address.Component.Kind.DISTRICT
                }
            }?.name

        val city = cityFromBusiness ?: cityFromToponym

        val place = Place(null, name, city, address, null, point.latitude, point.longitude)

        val id = "${name}_${point.latitude}_${point.longitude}".hashCode().toString()
        val exists = placeRepository.exists(id)
        if (!exists) {
            placeRepository.savePlace(place.copy(id = id))
        }

        return place
    }

    private fun toJtsPolygon(points: List<Point>): org.locationtech.jts.geom.Polygon {
        val coordinates = points.map {
            Coordinate(it.longitude, it.latitude)
        }.toTypedArray()

        // Ensure closed ring (first point = last)
        val closed = if (coordinates.first() != coordinates.last()) {
            coordinates + coordinates.first()
        } else coordinates

        return geometryFactory.createPolygon(closed)
    }
}
