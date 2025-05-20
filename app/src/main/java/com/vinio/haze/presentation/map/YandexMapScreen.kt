package com.vinio.haze.presentation.map

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.vinio.haze.R
import com.vinio.haze.domain.Place
import com.vinio.haze.presentation.map.InfoDialog.PoiInfoDialog
import com.vinio.haze.startLocation
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapType
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolygonMapObject
import com.yandex.mapkit.search.BusinessObjectMetadata
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.operation.union.CascadedPolygonUnion
import org.locationtech.jts.geom.Polygon as JtsPolygon

val geometryFactory = GeometryFactory()

@Composable
fun YandexMapScreen(
    modifier: Modifier = Modifier,
    viewModel: YandexMapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    val poiItems by viewModel.poiItems.collectAsState()
    val zoom by viewModel.zoomLevel.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    var selectedPlace by remember { mutableStateOf<Place?>(null) }

    val poiCollection = remember {
        mapView.mapWindow.map.mapObjects.addCollection().apply {
            addTapListener { mapObject, _ ->
                val place = mapObject.userData as? Place ?: return@addTapListener false
                Toast.makeText(context, place.name, Toast.LENGTH_SHORT).show()
                selectedPlace = place
                true
            }
        }
    }
    // Работа с туманов
    val fogCollection = remember { mapView.mapWindow.map.mapObjects.addCollection() }
    var fogPolygonObj by remember { mutableStateOf<PolygonMapObject?>(null) }
    val visibleAreas = remember { mutableStateListOf<LinearRing>() }

    val worldOuterRing = remember {
        LinearRing(
            listOf(
                Point(85.0, -180.0),
                Point(85.0, 180.0),
                Point(-85.0, 180.0),
                Point(-85.0, -180.0),
                Point(85.0, -180.0)
            )
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f),
            factory = { mapView }
        )
    }

    // Переместим камеру и выполним первичный поиск один раз
    LaunchedEffect(Unit) {
        mapView.mapWindow.map.mapType = MapType.MAP
        val target = Point(55.751225, 37.62954)
        mapView.mapWindow.map.move(CameraPosition(target, 17.0f, 0.0f, 0.0f))

        val region = mapView.mapWindow.map.visibleRegion
        val bbox = BoundingBox(region.bottomLeft, region.topRight)
        viewModel.requestSearch(bbox)

        // Инициализируем полигон
        fogPolygonObj = fogCollection.addPolygon(
            Polygon(worldOuterRing, visibleAreas.toList())
        ).apply {
            fillColor = 0xF0000000.toInt() // тёмный полупрозрачный
            strokeColor = 0x00000000.toInt()
            strokeWidth = 0f
            zIndex = 1f
        }
    }



    // Обновление маркеров при новых POI
    LaunchedEffect(poiItems, zoom) {
        poiCollection.clear()

        val scale = when {
            zoom >= 17 -> 1.0f
            zoom >= 15 -> 0.8f
            zoom >= 13 -> 0.6f
            else -> 0.4f
        }

        poiItems.forEachIndexed { index, item ->

            val geometryPoint = item.obj?.geometry?.firstOrNull()?.point ?: return@forEachIndexed
            val point = Point(geometryPoint.latitude, geometryPoint.longitude)
            val name = item.obj?.name?.toString().orEmpty()

            val metadata = item.obj?.metadataContainer
            val toponymAddress = metadata?.getItem(ToponymObjectMetadata::class.java)?.address?.formattedAddress
            val business = metadata?.getItem(BusinessObjectMetadata::class.java)
            val businessAddress = business?.address?.formattedAddress
            val address = businessAddress ?: toponymAddress

            val place = Place(name, address, null, point.latitude, point.longitude)

            val placemark = poiCollection.addPlacemark(point).apply {
                setIcon(ImageProvider.fromResource(context, R.drawable.ic_marker))
                userData = place
                setIconStyle(IconStyle().apply {
                    setScale(0f) // начинаем с 0, чтобы анимировать появление
                    anchor?.set(0.5f, 1.0f)
                })
            }

            // Плавная анимация появления с задержкой по индексу, чтобы POI появлялись постепенно
            launch {
                val steps = 10
                repeat(steps) { step ->
                    placemark.setIconStyle(IconStyle().apply {
                        setScale((step + 1) / steps.toFloat() * scale)
                        anchor?.set(0.5f, 1.0f)
                    })
                    delay(16L) // ~60fps
                }
                // Убедимся, что масштаб установлен в конечное значение
                placemark.setIconStyle(IconStyle().apply {
                    setScale(scale)
                    anchor?.set(0.5f, 1.0f)
                })
            }
        }
    }

    selectedPlace?.let { place ->
        PoiInfoDialog(place = place, onDismiss = { selectedPlace = null })
    }

    var userPlacemark by remember { mutableStateOf<PlacemarkMapObject?>(null) }
    val userPlacemarkState = remember { mutableStateOf<PlacemarkMapObject?>(null) }


    LaunchedEffect(userLocation) {
        userLocation?.let { point ->
            val placemark = userPlacemarkState.value
            if (placemark == null || !placemark.isValid) {
                val newPlacemark = mapView.mapWindow.map.mapObjects.addPlacemark(point).apply {
                    setIcon(ImageProvider.fromResource(context, R.drawable.ic_user_location))
                    setIconStyle(IconStyle().apply {
                        scale = 1.0f
                        anchor?.set(0.5f, 0.5f)
                    })
                }
                userPlacemarkState.value = newPlacemark
            } else {
                // Анимация перемещения
                animatePlacemarkMove(placemark, point)
            }

            mapView.mapWindow.map.move(
                CameraPosition(point, zoom, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1.0f),
                null
            )

            val newPolygon = makeSquarePolygon(point)
            val union = CascadedPolygonUnion.union(listOf(newPolygon) + visibleAreas.map { toJtsPolygon(it.points) })

            visibleAreas.clear()
            when (union) {
                is JtsPolygon -> visibleAreas.add(fromJtsPolygon(union))
                is org.locationtech.jts.geom.MultiPolygon -> {
                    for (i in 0 until union.numGeometries) {
                        visibleAreas.add(fromJtsPolygon(union.getGeometryN(i) as JtsPolygon))
                    }
                }
            }

            updateFogPolygon(fogPolygonObj, Polygon(worldOuterRing, visibleAreas.toList()))
        }
    }



    // Слушаем перемещение камеры и перезапускаем поиск
    DisposableEffect(mapView) {
        (context as? Activity)?.startLocation()
        val listener = com.yandex.mapkit.map.CameraListener { map, cameraPosition, update, finished ->
            if (finished) {
                val zoom = cameraPosition.zoom
                viewModel.onZoomChanged(zoom)

                val region = mapView.mapWindow.map.visibleRegion
                val bbox = BoundingBox(region.bottomLeft, region.topRight)
                viewModel.requestSearch(bbox)
            }
        }

        mapView.mapWindow.map.addCameraListener(listener)

        onDispose {
            Log.d("MapDebug", "CameraListener was removed")
            mapView.mapWindow.map.removeCameraListener(listener)
        }
    }
}

fun BoundingBox.toFormatString(): String {
    return "southWest: ${this.southWest.latitude}, ${this.southWest.longitude}\n" +
            "northEast: ${this.northEast.latitude}, ${this.northEast.longitude}"
}

private fun lerp(start: Double, end: Double, fraction: Float): Double =
    start + (end - start) * fraction

private suspend fun animatePlacemarkMove(placemark: PlacemarkMapObject, target: Point) {
    if (!placemark.isValid) return
    val start = placemark.geometry
    repeat(20) { step ->
        val t = (step + 1) / 20f
        placemark.geometry = Point(
            lerp(start.latitude, target.latitude, t),
            lerp(start.longitude, target.longitude, t)
        )
        delay(16)
    }
}

fun makeSquarePolygon(center: Point, sideMeters: Double = 300.0): JtsPolygon {
    val half = sideMeters / 2.0
    val dx = half / (40075000 * Math.cos(center.latitude * Math.PI / 180) / 360)
    val dy = half / 111320.0

    val points = listOf(
        Point(center.latitude - dy, center.longitude - dx),
        Point(center.latitude - dy, center.longitude + dx),
        Point(center.latitude + dy, center.longitude + dx),
        Point(center.latitude + dy, center.longitude - dx),
        Point(center.latitude - dy, center.longitude - dx) // замыкаем
    )
    return toJtsPolygon(points)
}

fun toJtsPolygon(points: List<Point>): JtsPolygon {
    val coordinates = points.map { Coordinate(it.longitude, it.latitude) }.toTypedArray()
    val shell = geometryFactory.createLinearRing(coordinates)
    return geometryFactory.createPolygon(shell)
}

fun fromJtsPolygon(jtsPolygon: JtsPolygon): LinearRing {
    return LinearRing(jtsPolygon.exteriorRing.coordinates.map { Point(it.y, it.x) })
}

fun updateFogPolygon(fog: PolygonMapObject?, polygon: Polygon) {
    if (fog == null) return
    fog.geometry = polygon
}

