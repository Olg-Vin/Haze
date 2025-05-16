package com.vinio.haze

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.map.MapType
import com.yandex.mapkit.mapview.MapView

@SuppressLint("ContextCastToActivity")
@Composable
fun YandexMapScreen() {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    AndroidView(
        factory = {
            mapView.mapWindow.map.mapType = MapType.MAP
            mapView
        },
        modifier = Modifier.fillMaxSize()
    )

    DisposableEffect(Unit) {
        mapView.onStart()
        onDispose {
            mapView.onStop()
        }
    }
}