package com.vinio.haze.presentation.map.InfoDialog

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
    val description = viewModel.description
    val isLoading = viewModel.isLoading

    LaunchedEffect(place) {
        viewModel.fetchDescription(place.name, place.lat, place.lon)
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