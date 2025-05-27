package com.vinio.haze.diAndUtils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
//  подписываемся на измененения статуса геолокации (вкл/выкл)
class LocationSettingsChangeReceiver(
    private val onLocationSettingsChanged: (Boolean) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        val isEnabled = LocationUtil.isLocationEnabled(context)
        onLocationSettingsChanged(isEnabled)
    }
}
