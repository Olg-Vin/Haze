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
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapType
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.search.BusinessObjectMetadata
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.delay

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
    }

    var selectedPlace by remember { mutableStateOf<Place?>(null) }

    // Обновление маркеров при новых POI
    LaunchedEffect(poiItems) {
        val map = mapView.mapWindow.map
        val rootCollection = map.mapObjects
        rootCollection.clear()
        val placemarkCollection = rootCollection.addCollection()

        Log.d("MapDebug", "Placing ${poiItems.size} placemarks")

        val scale = when {
            zoom >= 17 -> 1.0f
            zoom >= 15 -> 0.8f
            zoom >= 13 -> 0.6f
            else -> 0.4f
        }
        //  заполняем коллекцию меток полученными объектами
        poiItems.forEach { item ->

            val metadata = item.obj?.metadataContainer

            val toponym = metadata?.getItem(ToponymObjectMetadata::class.java)
            val toponymAddress = toponym?.address?.formattedAddress
            if (toponymAddress != null) {
                Log.d("MetaDebug", "Toponym Address: $toponymAddress")
            }

            val business = metadata?.getItem(BusinessObjectMetadata::class.java)
            val businessAddress = business?.address?.formattedAddress
            if (business != null) {
                Log.d("MetaDebug", "Business Name: ${business.name}")
                Log.d("MetaDebug", "Business Address: $businessAddress")
            }

            val address = businessAddress ?: toponymAddress

            val geometryPoint = item.obj?.geometry?.firstOrNull()?.point ?: return@forEach
            val point = Point(geometryPoint.latitude, geometryPoint.longitude)
            val name = item.obj?.name?.toString().orEmpty()
            val place = Place(name, address, null, point.latitude, point.longitude)

            placemarkCollection.addPlacemark().apply {
                geometry = point
                setIcon(ImageProvider.fromResource(context, R.drawable.ic_marker))
                userData = place
                setIconStyle(IconStyle().apply {
                    this.scale = scale
                    this.anchor?.set(0.5f, 1.0f)
                })
            }
        }
        //  для каждого элемента коллекции создаём слушатель тапов
        placemarkCollection.addTapListener { mapObject, _ ->
            val place = mapObject.userData as? Place ?: return@addTapListener false
            Toast.makeText(context, place.name, Toast.LENGTH_SHORT).show()
            selectedPlace = place
            true
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
