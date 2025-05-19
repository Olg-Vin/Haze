package com.vinio.haze.infrastructure

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.vinio.haze.domain.LocationClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class DefaultLocationClient(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationClient {

    companion object {
        private const val TAG = "DefaultLocationClient"
    }

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(intervalMs: Long): Flow<Location> = callbackFlow {
        // Проверка разрешений: ACCESS_FINE_LOCATION (и по необходимости ACCESS_BACKGROUND_LOCATION)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            throw LocationClient.LocationException("Нет разрешения на геолокацию")
        }
        // (По условиям: если нужны фоновые обновления, следует запросить ACCESS_BACKGROUND_LOCATION:contentReference[oaicite:1]{index=1} или использовать Foreground Service)

        // Настраиваем LocationRequest с интервалом intervalMs и высокой точностью
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
            // Fastest interval (минимальный период между обновлениями) – равный desired-interval
            .setMinUpdateIntervalMillis(intervalMs)
            // Без батчинга: доставка каждого обновления сразу
            .setMaxUpdateDelayMillis(0L)
            // Обновлять даже без перемещения устройства
            .setMinUpdateDistanceMeters(0F)
            .build()

        // Колбэк для приёма обновлений
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                val location = result.lastLocation ?: return
//                Log.d(TAG, "onLocationResult: location=$location")
                // Отправляем координаты в поток
                trySend(location)
            }
        }

        Log.d(TAG, "requestLocationUpdates(intervalMs=$intervalMs)")
        // Запрашиваем обновления (Looper может быть null, тогда используется поток вызова)
        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )

        // Корректно отписываемся при закрытии потока
        awaitClose {
            Log.d(TAG, "awaitClose: removeLocationUpdates")
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}
