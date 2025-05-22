package com.vinio.haze.presentation.screens.cityScreens.cityDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinio.haze.domain.ai.AiRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityDetailsViewModel @Inject constructor(
    private val aiRequest: AiRequest
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _imageUrl = MutableStateFlow("")
    val imageUrl: StateFlow<String> = _imageUrl

    fun fetchCityDetails(cityName: String) {
        viewModelScope.launch {
            _name.value = cityName
            _description.value = ""
            _imageUrl.value = ""
            isLoading = true

            try {
                // Запрашиваем описание
                aiRequest.streamPoiDescription(cityName, 0.0, 0.0).collectLatest { token ->
                    _description.value += token
                }

                // Запрашиваем изображение (если реализовано)
//                _imageUrl.value = aiRequest.getCityImageUrl(cityName)

            } finally {
                isLoading = false
            }
        }
    }
}