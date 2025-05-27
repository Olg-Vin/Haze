package com.vinio.haze.presentation.startScreen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.vinio.haze.R

@Composable
fun StartScreen(onPermissionsGranted: () -> Unit) {
    var showInfoPopup by remember { mutableStateOf(false) }
    var showSecondAttemptDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showBackgroundPermissionDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    // Foreground permissions: точное местоположение и уведомления
    val foregroundPermissions = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Background permission (Android 10+)
    val backgroundPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            Manifest.permission.ACCESS_BACKGROUND_LOCATION else null

    var foregroundGranted by remember { mutableStateOf(false) }
    var backgroundGranted by remember { mutableStateOf(false) }
    var backgroundRequested by remember { mutableStateOf(false) }

    // Запуск запроса foreground разрешений
    val foregroundLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (locationGranted) {
            foregroundGranted = true
        } else {
            val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                activity ?: return@rememberLauncherForActivityResult,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            if (showRationale) {
                showSecondAttemptDialog = true
            } else {
                showSettingsDialog = true
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] == true
            Toast.makeText(
                context,
                "Геолокация: ${if (locationGranted) "OK" else "Нет"}, " +
                        "Уведомления: ${if (notificationGranted) "OK" else "Нет"}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Запуск запроса background разрешения
    val backgroundLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            backgroundGranted = true
            Toast.makeText(context, "Фоновая геолокация разрешена", Toast.LENGTH_SHORT).show()
            onPermissionsGranted()
        } else {
            showSettingsDialog = true
        }
    }

    // Когда foreground разрешение получено, запрашиваем background (после подтверждения диалогом)
    LaunchedEffect(foregroundGranted, backgroundRequested) {
        if (foregroundGranted && !backgroundGranted && !backgroundRequested && backgroundPermission != null) {
            showBackgroundPermissionDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(id = R.drawable.start_img),
                contentDescription = "Sample Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Haze",
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Описание приложения
            Text(
                text = "Если вы любите погулять пешком\nи исходили уже весь свой город – это приложение для вас!\n" +
                        "Время открыть для себя родные края заново",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // Сиреневая кнопка через цвета Material
            Button(
                onClick = {
                    if (!foregroundGranted) {
                        showInfoPopup = true // Показать информационный диалог, не запрашивать разрешения сразу
                    } else if (!backgroundGranted && backgroundPermission != null) {
                        showBackgroundPermissionDialog = true
                    } else {
                        onPermissionsGranted()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD1C4E9)
                )
            ) {
                Text(
                    text = "Начнём!",
                    fontSize = 22.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }

            // Диалог повторного запроса
            if (showSecondAttemptDialog) {
                AlertDialog(
                    onDismissRequest = { showSecondAttemptDialog = false },
                    title = { Text("Разрешение на геолокацию") },
                    text = { Text("Пожалуйста, предоставьте разрешение на геолокацию для корректной работы приложения.") },
                    confirmButton = {
                        Button(onClick = {
                            showSecondAttemptDialog = false
                            foregroundLauncher.launch(foregroundPermissions.toTypedArray())
                        }) {
                            Text("Повторить")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSecondAttemptDialog = false }) {
                            Text("Отмена")
                        }
                    }
                )
            }

            // Диалог с переходом в настройки (для отказавших)
            if (showSettingsDialog) {
                AlertDialog(
                    onDismissRequest = { showSettingsDialog = false },
                    title = { Text("Разрешение отклонено") },
                    text = {
                        Text("Вы навсегда запретили доступ к геолокации. Чтобы включить его, откройте настройки приложения.")
                    },
                    confirmButton = {
                        Button(onClick = {
                            showSettingsDialog = false
                            activity?.openAppSettings()
                        }) {
                            Text("Открыть настройки")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSettingsDialog = false }) {
                            Text("Отмена")
                        }
                    }
                )
            }

            // Диалог с объяснением необходимости фонового разрешения
            if (showBackgroundPermissionDialog) {
                AlertDialog(
                    onDismissRequest = { showBackgroundPermissionDialog = false },
                    title = { Text("Разрешение на геолокацию в фоне") },
                    text = {
                        Text("Для корректной работы приложения в фоне необходимо разрешить доступ к геолокации всегда. Пожалуйста, предоставьте это разрешение.")
                    },
                    confirmButton = {
                        Button(onClick = {
                            showBackgroundPermissionDialog = false
                            backgroundRequested = true
                            backgroundLauncher.launch(backgroundPermission!!)
                        }) {
                            Text("Разрешить")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showBackgroundPermissionDialog = false }) {
                            Text("Отмена")
                        }
                    }
                )
            }
        }
    }
    if (showInfoPopup) {
        PermissionsInfoPopup(
            showDialog = showInfoPopup,
            onDismiss = { showInfoPopup = false },
            onOkClick = {
                showInfoPopup = false
                foregroundLauncher.launch(foregroundPermissions.toTypedArray())
            }
        )
    }
}

// Вспомогательная функция открытия настроек
fun Activity.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}
