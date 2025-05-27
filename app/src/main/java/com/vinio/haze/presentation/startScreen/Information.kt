package com.vinio.haze.presentation.startScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PermissionsInfoPopup(
    showDialog: Boolean,
    onOkClick: () -> Unit,
) {
    var geoExpanded by remember { mutableStateOf(false) }
    var notifExpanded by remember { mutableStateOf(false) }

    if (showDialog) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 350.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {

                Text(
                    text = "Перед тем как начать…",
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Для работы этого приложения потребуется несколько разрешений:",
                    fontSize = 16.sp,
                    color = Color.Black,
                )

                Spacer(modifier = Modifier.height(16.dp))

                PermissionItem(
                    title = "Геолокация",
                    expanded = geoExpanded,
                    onClick = { geoExpanded = !geoExpanded },
                    description = "Геолокация понадобится чтобы запомнить весь ваш путь и не пропустить ни шагу прогулки"
                )

                PermissionItem(
                    title = "Уведомления",
                    expanded = notifExpanded,
                    onClick = { notifExpanded = !notifExpanded },
                    description = "Геолокация понадобится чтобы запомнить весь ваш путь и не пропустить ни шагу прогулки"
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onOkClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD1C4E9)
                    ),
                    elevation = null
                ) {
                    Text(
                        text = "Окей!",
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionItem(
    title: String,
    expanded: Boolean,
    onClick: () -> Unit,
    description: String
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onClick() }
                .padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown
                                else Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontSize = 17.sp,
                color = Color.Black
            )
        }
        if (expanded) {
            Text(
                text = description, fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 28.dp, end = 2.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
    }
}