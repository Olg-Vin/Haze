package com.vinio.haze.presentation.screens.achievementsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vinio.haze.presentation.navigation.Screen
import kotlin.math.pow

@Composable
fun AchievementsScreen(
    navController: NavController,
    viewModel: AchievementsViewModel = hiltViewModel()
) {
    val currentCount by viewModel.currentCount.collectAsState()

    val base = 50
    val factor = 2.0

    val achievements = remember(currentCount) {
        List(10) { index ->
            val threshold = (base * factor.pow(index.toDouble())).toInt()
            Achievement(title = "Открыто $threshold POIs", total = threshold)
        }
    }

    val notReached = achievements.filter { currentCount < it.total }

    val toShow = buildList {
        val lastReachedIndex = achievements.indexOfLast { currentCount >= it.total }
        if (lastReachedIndex != -1) {
            add(achievements[lastReachedIndex])
        }
        addAll(notReached.take(1)) // показываем до 3 следующих достижений
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                "Достижения",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(toShow) { achievement ->
                    AchievementItem(
                        title = achievement.title,
                        current = currentCount.coerceAtMost(achievement.total),
                        total = achievement.total
                    )
                }
            }
        }

        IconButton(
            onClick = { navController.navigate(Screen.Map.route) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Открыть карту",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

data class Achievement(val title: String, val total: Int)

@Composable
fun AchievementItem(title: String, current: Int, total: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD9D9D9), RoundedCornerShape(12.dp)) // более серый фон
            .padding(16.dp)
            .height(48.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 18.sp
            )
            Text(
                text = "$current / $total",
                color = if (current >= total) Color.Blue else Color.Black, // синий если выполнено
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        if (current < total) {
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            LinearProgressIndicator(
                progress = { current.toFloat() / total },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFF1565C0),
                trackColor = Color(0xFFB0BEC5),
            )
        }
    }
}

