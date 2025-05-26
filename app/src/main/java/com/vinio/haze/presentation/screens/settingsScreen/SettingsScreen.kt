package com.vinio.haze.presentation.screens.settingsScreen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vinio.haze.R
import kotlin.math.absoluteValue

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val avatarUri by viewModel.avatarUri.collectAsState()
    val username by viewModel.username.collectAsState()
    val fogOpacity by viewModel.fogOpacity.collectAsState()
    val showPOI by viewModel.showPOI.collectAsState()
    val progress = 0.7f
    val notifyAchievements by viewModel.notifyAchievements.collectAsState()
    val fogColor by viewModel.fogColor.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { viewModel.setAvatarUri(it) } }

    val ringColor = Color.Black
    val circleRadius = 64.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 18.dp)
    ) {
        // Ранг, буква, точки

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 18.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 12.dp)
            ) {
                Canvas(
                    modifier = Modifier.size(circleRadius * 2)
                ) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val smallRadius = size.width / 2f - 10.dp.toPx()
                    for (i in 0 until 12) {
                        val angle = Math.toRadians(i * 30.0)
                        drawCircle(
                            color = ringColor,
                            radius = 3.dp.toPx(),
                            center = Offset(
                                (cx + smallRadius * kotlin.math.cos(angle)).toFloat(),
                                (cy + smallRadius * kotlin.math.sin(angle)).toFloat()
                            )
                        )
                    }
                }
                Text(
                    text = "F",
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                Modifier.padding(top = 50.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Ранг")
                Text("Исследователя")
            }
        }

        UserProfileCard(
            username = username,
            avatarUri = avatarUri,
            progress = progress,
            onNameChange = { viewModel.setUsername(it) },
            onAvatarClick = { launcher.launch("image/*") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FogColorSelector (
            selectedColor = fogColor,
            onColorSelected = { viewModel.setFogColor(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))
        // Слайдер для тумана
        FogOpacitySlider(
            value = fogOpacity,
            onValueChange = { viewModel.setFogOpacity(it) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CheckBoxRow(
            text = "Показывать точки интереса",
            checked = showPOI,
            onCheckedChange = { viewModel.setShowPOI(it) }
        )

        CheckBoxRow(
            text = "Уведомлять о достижениях",
            checked = notifyAchievements,
            onCheckedChange = { viewModel.setNotifyAchievements(it) }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FogOpacitySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember { mutableFloatStateOf(value) }

    LaunchedEffect(value) {
        if (value != sliderPosition) {
            sliderPosition = value
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "Прозрачность тумана ${sliderPosition.toInt()}%",
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(4.dp))
        Slider(
            value = value,
            onValueChange = {
                sliderPosition = it
                onValueChange(it)
            },
            valueRange = 0f..100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            colors = SliderDefaults.colors(
                activeTrackColor = Color(0xFFD8D8D8),
                inactiveTrackColor = Color(0xFFD8D8D8),
                thumbColor = Color.White
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                        .border(2.dp, Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) { }
            },
            track = { state ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFD8D8D8))
                )
            }
        )
    }
}


@Composable
fun CheckBoxRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal
        )
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Canvas(Modifier.size(28.dp)) {
                    drawLine(
                        color = Color.Black,
                        start = Offset(size.width * 0.2f, size.height * 0.5f),
                        end = Offset(size.width * 0.45f, size.height * 0.8f),
                        strokeWidth = 4f
                    )
                    drawLine(
                        color = Color.Black,
                        start = Offset(size.width * 0.45f, size.height * 0.8f),
                        end = Offset(size.width * 0.8f, size.height * 0.2f),
                        strokeWidth = 4f
                    )
                }
            }
        }
    }
}

@Composable
fun FogColorSelector(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var popupVisible by remember { mutableStateOf(false) }
    val colorOptions = listOf(
        Color(0xFFD3D3D3),
        Color(0xFFD32F2F),
        Color(0xFF9575CD),
        Color(0xFF43EA36),
        Color(0xFFE573D3),
        Color(0xFF282425)
    )

    Column(modifier = modifier) {
        Text("Цвет тумана", fontSize = 18.sp)
        Box(
            Modifier
                .fillMaxWidth()
                .height(36.dp)
                .background(selectedColor, RoundedCornerShape(8.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                .clickable { popupVisible = true }
        )

        if (popupVisible) {
            Dialog(
                onDismissRequest = { popupVisible = false }
            ) {
                Box(
                    Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row {
                            ColorOption(colorOptions[0], onPick = {
                                onColorSelected(colorOptions[0])
                                popupVisible = false
                            })
                            Spacer(Modifier.width(16.dp))
                            ColorOption(colorOptions[1], onPick = {
                                onColorSelected(colorOptions[1])
                                popupVisible = false
                            })
                        }
                        Spacer(Modifier.height(8.dp))
                        Row {
                            ColorOption(colorOptions[2], onPick = {
                                onColorSelected(colorOptions[2])
                                popupVisible = false
                            })
                            Spacer(Modifier.width(16.dp))
                            ColorOption(colorOptions[3], onPick = {
                                onColorSelected(colorOptions[3])
                                popupVisible = false
                            })
                        }
                        Spacer(Modifier.height(8.dp))
                        Row {
                            ColorOption(colorOptions[4], onPick = {
                                onColorSelected(colorOptions[4])
                                popupVisible = false
                            })
                            Spacer(Modifier.width(16.dp))
                            ColorOption(colorOptions[5], onPick = {
                                onColorSelected(colorOptions[5])
                                popupVisible = false
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorOption(color: Color, onPick: () -> Unit) {
    Box(
        Modifier
            .size(width = 120.dp, height = 44.dp)
            .background(color, RoundedCornerShape(12.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
            .clickable { onPick() }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileCard(
    username: String?,
    avatarUri: Uri?,
    progress: Float,
    onNameChange: (String) -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var editValue by remember { mutableStateOf(username ?: "") }

    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditing) {
                TextField(
                    value = editValue,
                    onValueChange = { editValue = it },
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(fontSize = 32.sp),
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                    singleLine = true
                )
                IconButton(
                    onClick = {
                        onNameChange(editValue)
                        isEditing = false
                    }
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Сохранить")
                }
                IconButton(
                    onClick = {
                        editValue = username ?: ""
                        isEditing = false
                    }
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Отмена")
                }
            } else {
                Text(
                    username ?: "",
                    fontSize = 32.sp,
                )
                IconButton(onClick = {
                    editValue = username ?: ""
                    isEditing = true
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        val avatarSize = 64.dp
        val corner = 22.dp

        Row(
            modifier = Modifier
                .height(avatarSize)
        ) {
            Box(
                modifier = Modifier
                    .size(avatarSize)
                    .zIndex(2f)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(topStart = corner, bottomStart = corner))
                        .border(
                            2.dp,
                            Color.Black,
                            RoundedCornerShape(topStart = corner, bottomStart = corner)
                        )
                        .background(Color.White)
                        .clickable { onAvatarClick() }
                )
                if (avatarUri != null) {
                    AsyncImage(
                        model = avatarUri,
                        contentDescription = "Аватар",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(topStart = corner, bottomStart = corner))
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Плейсхолдер",
                        modifier = Modifier
                            .align(Alignment.Center).size(40.dp)
                            .alpha(0.4f)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .height(avatarSize)
                    .weight(1f)
                    .offset(x = (-2).dp)
                    .clip(RoundedCornerShape(topEnd = corner, bottomEnd = corner))
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(topEnd = corner, bottomEnd = corner)
                    )
                    .background(Color(0xFFEAEAEA))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(topEnd = corner, bottomEnd = corner))
                        .background(Color(0xFF9575CD))
                )
            }
        }
    }
}

