package com.vinio.haze

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapType
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

@Composable
fun YandexMapScreen(
    modifier: Modifier = Modifier,
    viewModel: YandexMapViewModel = YandexMapViewModel()
) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    val poiItems by viewModel.poiItems.collectAsState()

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

        poiItems.forEach { item ->
            val point = item.obj?.geometry?.firstOrNull()?.point ?: return@forEach
            val name = item.obj?.name.orEmpty()
            val placemark = placemarkCollection.addPlacemark(point)
            placemark.setIcon(ImageProvider.fromResource(context, R.drawable.ic_marker))
            placemark.userData = name
        }

        placemarkCollection.addTapListener { obj, _ ->
            val name = obj.userData as? String ?: return@addTapListener false
            Toast.makeText(context, name, Toast.LENGTH_SHORT).show()
            true
        }
    }

    // Слушаем перемещение камеры и перезапускаем поиск
    DisposableEffect(mapView) {
        val listener = com.yandex.mapkit.map.CameraListener { _, _, _, finished ->
            if (finished) {
                val region = mapView.mapWindow.map.visibleRegion
                val bbox = BoundingBox(region.bottomLeft, region.topRight)
                viewModel.requestSearch(bbox)
            }
        }

        mapView.mapWindow.map.addCameraListener(listener)

        onDispose {
            mapView.mapWindow.map.removeCameraListener(listener)
        }
    }
}

