package com.vinio.haze

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vinio.haze.domain.location.LocationService
import com.vinio.haze.ui.theme.HazeTheme
import com.vinio.haze.presentation.navigation.AppNavigation
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HazeTheme {
                AppNavigation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}

fun Activity.startLocation() {
    Log.d("Location","Start location intent")
    Intent(applicationContext, LocationService::class.java).apply {
        action = LocationService.ACTION_START
        startService(this)
    }
}

fun Activity.stopLocation() {
    Log.d("Location","Stop location intent")
    Intent(applicationContext, LocationService::class.java).apply {
        action = LocationService.ACTION_STOP
        startService(this)
    }
}