package com.vinio.haze.presentation.map.InfoDialog

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vinio.haze.domain.Place

@Composable
fun PoiInfoDialog(
    place: Place,
    onDismiss: () -> Unit,
    viewModel: PoiInfoViewModel = hiltViewModel<PoiInfoViewModel>(),
) {
//    val description = viewModel.description
    val isLoading = viewModel.isLoading

    val description by viewModel.description.collectAsState()

    LaunchedEffect(place) {
//        viewModel.fetchDescription(place.name + place.address, place.lat, place.lon)
        viewModel.fetchStreamingDescription(place.name + place.address, place.lat, place.lon)
    }

    LaunchedEffect(description) {
        Log.d("Compose", "Description updated: $description")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(place.name) },
        text = {
            if (isLoading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Получение описания...")
                }
                Text(description)
            } else {
                Text(description)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}