package com.vinio.haze.application.useCases

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.vinio.haze.diAndUtils.LocationUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class ObserveLocationEnabledUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun execute(): Flow<Boolean> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                Log.d("LocationReceiver", "onReceive: intent action = ${intent?.action}")
                val enabled = LocationUtil.isLocationEnabled(ctx ?: context)
                Log.d("LocationReceiver", "Location enabled = $enabled")
                trySend(enabled).isSuccess
            }
        }

        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)

        context.registerReceiver(receiver, filter)

        trySend(LocationUtil.isLocationEnabled(context)).isSuccess

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }.distinctUntilChanged()
}

