package com.vinio.haze.presentation.map.InfoDialog

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vinio.haze.domain.Place
import kotlinx.coroutines.delay

@Composable
fun PoiInfoDialog(
    place: Place,
    onDismiss: () -> Unit,
    viewModel: PoiInfoViewModel = hiltViewModel<PoiInfoViewModel>(),
) {
    val isLoading = viewModel.isLoading
    val fullDescription by viewModel.description.collectAsState()

    var displayedDescription by remember { mutableStateOf("") }

    LaunchedEffect(place) {
        viewModel.fetchStreamingDescription(place.name + place.address, place.lat, place.lon)
        displayedDescription = "" // сброс перед новым запросом
    }

    LaunchedEffect(fullDescription) {
        val oldLength = displayedDescription.length
        val newText = fullDescription.drop(oldLength)
        for (char in newText) {
            displayedDescription += char
            delay(30) // скорость печати можно настроить
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(place.name) },
        text = {
            Column {
                if (isLoading) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Получение описания...")
                    }
                    Spacer(Modifier.height(8.dp))
                }
                Text(displayedDescription)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
