package com.vinio.haze.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.vinio.haze.diAndUtils.hasLocationPermission
import com.vinio.haze.diAndUtils.hasNotificationPermission


/**
 * Если все необходимые разрешения предоставлены, в данном случае геолокация,
 * то пользователя следует перенаправить сразу на MapScreen() в обход StartScreen()
 * */
@Composable
fun PermissionCheckScreen(
    onPermissionsGranted: () -> Unit,
    onPermissionsNotGranted: () -> Unit
) {
    val context = LocalContext.current

    val fineLocationGranted = context.hasLocationPermission() && context.hasNotificationPermission()

    LaunchedEffect(Unit) {
        if (fineLocationGranted) {
            onPermissionsGranted()
        } else {
            onPermissionsNotGranted()
        }
    }
}
