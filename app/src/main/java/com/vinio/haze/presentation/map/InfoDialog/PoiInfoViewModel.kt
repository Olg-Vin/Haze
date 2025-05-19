package com.vinio.haze.presentation.map.InfoDialog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinio.haze.domain.AiRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PoiInfoViewModel @Inject constructor(
    private val aiRequest: AiRequest
) : ViewModel() {

    var description by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun fetchDescription(name: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            isLoading = true
            description = aiRequest.getPoiDescription(name, lat, lon)
            isLoading = false
        }
    }
}
