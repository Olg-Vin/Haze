package com.vinio.haze

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val apiKey = getMetaDataValue("com.yandex.android.maps.apikey")
        Log.d("MyApplication", "Yandex API key: $apiKey")

        MapKitFactory.setLocale("ru_RU")
        MapKitFactory.setApiKey(apiKey)
        MapKitFactory.initialize(this)
    }

    private fun getMetaDataValue(name: String): String {
        val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        return appInfo.metaData?.getString(name)
            ?: throw IllegalStateException("API key not found in manifest")
    }
}