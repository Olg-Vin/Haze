package com.vinio.haze.presentation.startScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PermissionsInfoPopup(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
) {

    var showLocationInfo by remember { mutableStateOf(false) }
    var showNotificationInfo by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = { onOkClick() }) {
                    Text("ОК")
                }
            },
            title = {
                Text(
                    text = "Необходимые разрешения",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column {
                    Text("Чтобы обеспечить корректную работу приложения, нам нужны следующие разрешения:")

                    Spacer(modifier = Modifier.height(16.dp))

                    // Геолокация
                    Text(
                        text = "• Геолокация",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { showLocationInfo = !showLocationInfo }
                    )
                    if (showLocationInfo) {
                        Text(
                            text = "Требуется для отображения вашего местоположения на карте и поиска ближайших точек.",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Уведомления
                    Text(
                        text = "• Уведомления",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            showNotificationInfo = !showNotificationInfo
                        }
                    )
                    if (showNotificationInfo) {
                        Text(
                            text = "Требуются для получения оповещений о важных событиях и обновлениях.",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }
            }
        )
    }
}
