package com.vinio.haze

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val apiKey = getMetaDataValue("com.yandex.android.maps.apikey")

        MapKitFactory.setLocale("ru_RU")
        MapKitFactory.setApiKey(apiKey)
        MapKitFactory.initialize(this)

        val channel = NotificationChannel(
            "location",
            "Location",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun getMetaDataValue(name: String): String {
        val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        return appInfo.metaData?.getString(name)
            ?: throw IllegalStateException("API key not found in manifest")
    }
}