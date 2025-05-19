package com.vinio.haze.presentation.map.InfoDialog

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinio.haze.domain.AiRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PoiInfoViewModel @Inject constructor(
    private val aiRequest: AiRequest
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    fun fetchStreamingDescription(name: String, lat: Double, lon: Double) {
        viewModelScope.launch {
            isLoading = true
            _description.value = ""
            try {
                aiRequest.streamPoiDescription(name, lat, lon)
                    .collectLatest { token ->
                        Log.d("PoiInfoViewModel", "Received token: $token")
                        _description.value += token
                    }
            } finally {
                isLoading = false
            }
        }
    }
}
