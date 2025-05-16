package com.vinio.haze.presentation.map

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.vinio.haze.R
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapType
import com.yandex.runtime.image.ImageProvider

@Composable
fun YandexMapScreen(
    modifier: Modifier = Modifier,
    viewModel: YandexMapViewModel = hiltViewModel<YandexMapViewModel>()
) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    val poiItems by viewModel.poiItems.collectAsState()
    val zoom by viewModel.zoomLevel.collectAsState()

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

        poiItems.forEach { item ->
            val point = item.obj?.geometry?.firstOrNull()?.point ?: return@forEach
            val name = item.obj?.name.orEmpty()
            placemarkCollection.addPlacemark().apply {
                geometry = point
                setIcon(ImageProvider.fromResource(context, R.drawable.ic_marker))
                userData = name
                setIconStyle(IconStyle().apply {
                    this.scale = scale
                    this.anchor?.set(0.5f, 1.0f)
                })
            }
        }

        placemarkCollection.addTapListener { obj, _ ->
            val name = obj.userData as? String ?: return@addTapListener false
            Toast.makeText(context, name, Toast.LENGTH_SHORT).show()
            true
        }
    }

    // Слушаем перемещение камеры и перезапускаем поиск
    DisposableEffect(mapView) {
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