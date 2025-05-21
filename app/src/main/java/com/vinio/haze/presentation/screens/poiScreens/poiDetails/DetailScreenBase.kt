package com.vinio.haze.presentation.screens.poiScreens.poiDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun DetailScreenBase(
    title: String,
    onFetch: () -> Unit,
    isLoading: Boolean,
    description: String
) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        onFetch()
    }

    LaunchedEffect(description) {
        val newText = description.drop(displayedText.length)
        for (char in newText) {
            displayedText += char
            delay(25L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        if (isLoading) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Получение описания...")
            }
            Spacer(Modifier.height(8.dp))
        }

        Text(displayedText, style = MaterialTheme.typography.bodyLarge)
    }
}
