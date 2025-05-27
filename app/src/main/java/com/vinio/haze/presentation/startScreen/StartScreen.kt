package com.vinio.haze.presentation.startScreen

import android.Manifest
import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.vinio.haze.R
import com.vinio.haze.diAndUtils.openAppSettings

@Composable
fun StartScreen(onPermissionsGranted: () -> Unit) {
    var showInfoPopup by remember { mutableStateOf(false) }
    var showSecondAttemptDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    val permissionList = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
//  TODO  добавити уведомления как обязательное разрешение
    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

        if (locationGranted) {
            onPermissionsGranted()
        } else {
            val showRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Sample Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 16f)
                    .clip(RoundedCornerShape(16.dp)),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Это приложение позволит вам оценить свой прогресс исследования мира!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = { showInfoPopup = true },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Начнём!")
            }

            PermissionsInfoPopup(
                showDialog = showInfoPopup,
                onDismiss = { showInfoPopup = false },
                onOkClick = {
                    permissionsLauncher.launch(permissionList.toTypedArray())
                    showInfoPopup = false
                }
            )

            // Диалог для повторного запроса
            if (showSecondAttemptDialog) {
                AlertDialog(
                    onDismissRequest = { showSecondAttemptDialog = false },
                    title = { Text("Разрешение на геолокацию") },
                    text = { Text("Пожалуйста, предоставьте разрешение на геолокацию для корректной работы приложения.") },
                    confirmButton = {
                        Button(onClick = {
                            showSecondAttemptDialog = false
                            permissionsLauncher.launch(permissionList.toTypedArray())
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

            // Диалог с переходом в настройки
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
        }
    }
}