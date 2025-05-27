package com.vinio.haze.diAndUtils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.vinio.haze.domain.location.LocationService

fun Context.hasLocationPermission(): Boolean {
    val backgroundPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
//            && backgroundPermission
}

fun Context.hasNotificationPermission(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
    return true
}


fun Activity.startLocation() {
    Log.d("Location","Start location intent")
    Intent(
        applicationContext,
        LocationService::class.java
    ).apply {
        action = LocationService.ACTION_START
        startService(this)
    }
}

fun Activity.stopLocation() {
    Log.d("Location","Stop location intent")
    Intent(
        applicationContext,
        LocationService::class.java
    ).apply {
        action = LocationService.ACTION_STOP
        startService(this)
    }
}
