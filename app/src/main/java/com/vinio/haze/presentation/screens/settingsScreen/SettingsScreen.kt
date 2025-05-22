package com.vinio.haze.presentation.screens.settingsScreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val avatarUri by viewModel.avatarUri.collectAsState()
    val username by viewModel.username.collectAsState()
    val fogOpacity by viewModel.fogOpacity.collectAsState()
    val showPOI by viewModel.showPOI.collectAsState()
    val showDialog = remember { mutableStateOf(false) }

    var usernameInput by remember { mutableStateOf(username ?: "") }
    val isUsernameChanged = usernameInput != (username ?: "")

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setAvatarUri(it) }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (avatarUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(avatarUri),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .clickable { launcher.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .clickable { launcher.launch("image/*") }
                        .padding(12.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = usernameInput,
                    onValueChange = { usernameInput = it },
                    label = { Text("Имя пользователя") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                if (isUsernameChanged) {
                    IconButton(
                        onClick = { viewModel.setUsername(usernameInput) },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Сохранить имя",
                            tint = Color.Green
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )
        Text(
            "Настройки",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text("Прозрачность тумана войны: ${fogOpacity.toInt()}%")
        FogOpacitySlider(
            fogOpacity = fogOpacity,
            onFogOpacityChange = { viewModel.setFogOpacity(it) }
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // Show POI toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Показ POI на карте")
            Switch(
                checked = showPOI,
                onCheckedChange = { viewModel.setShowPOI(it) }
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // Reset progress
        Button(
            onClick = { showDialog.value = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red.copy(alpha = 0.8f),
                contentColor = Color.White
            )
        ) {
            Text("Сбросить прогресс", color = Color.White)
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text("Подтвердите действие") },
                text = { Text("Вы уверены, что хотите сбросить весь прогресс? Это действие необратимо.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.resetProgress()
                        showDialog.value = false
                    }) {
                        Text("Да")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

@Composable
fun FogOpacitySlider(
    fogOpacity: Float,
    onFogOpacityChange: (Float) -> Unit
) {
    var sliderPosition by remember { mutableFloatStateOf(fogOpacity) }

    LaunchedEffect(fogOpacity) {
        if (fogOpacity != sliderPosition) {
            sliderPosition = fogOpacity
        }
    }

    Slider(
        value = sliderPosition,
        onValueChange = {
            sliderPosition = it
        },
        onValueChangeFinished = {
            onFogOpacityChange(sliderPosition)
        },
        valueRange = 0f..100f
    )
}

