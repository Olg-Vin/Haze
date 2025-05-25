package com.vinio.haze.presentation.screens.poiScreens.poiDetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinio.haze.domain.adapter.AiRequest
import com.vinio.haze.domain.model.Place
import com.vinio.haze.domain.repository.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PoiDetailsViewModel @Inject constructor(
    private val placeRepository: PlaceRepository,
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

    private var currentPlace: Place? = null

    fun fetchPlaceDetails(poiId: String, isCityMode: Boolean = false) {
        viewModelScope.launch {
            val place = placeRepository.getPlaceById(poiId)
            currentPlace = place

            val resolvedName = if (isCityMode) place.city ?: place.name else place.name
            _name.value = resolvedName

            val lat = place.lat
            val lon = place.lon

            if (!isCityMode && !place.description.isNullOrBlank()) {
                _description.value = place.description
                return@launch
            }

            isLoading = true
            _description.value = ""

            try {
                aiRequest.streamPoiDescription(resolvedName, lat, lon)
                    .collectLatest { token ->
                        _description.value += token
                    }

                if (!isCityMode) {
                    placeRepository.updatePlaceDescription(place.id!!, _description.value)
                }

            } finally {
                isLoading = false
            }
        }
    }
}

