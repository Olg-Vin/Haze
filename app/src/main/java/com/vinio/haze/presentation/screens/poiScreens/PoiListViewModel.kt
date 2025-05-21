package com.vinio.haze.presentation.screens.poiScreens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinio.haze.domain.model.Place
import com.vinio.haze.domain.repository.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PoiListViewModel @Inject constructor(
    private val repository: PlaceRepository
) : ViewModel() {
    private val _places = MutableStateFlow<List<Place>>(emptyList())
    val places: StateFlow<List<Place>> = _places

    private val _cities = MutableStateFlow<List<String>>(emptyList())
    val cities: StateFlow<List<String>> = _cities

    // Выбранный город (пустая строка = все города)
    private val _selectedCity = MutableStateFlow("")
    val selectedCity: StateFlow<String> = _selectedCity

    init {
        viewModelScope.launch {
            // Загрузка всех мест
            repository.getAllPlaces().collect { placeList ->
                _places.value = placeList

                // Обновить список городов из мест
                val cityList = placeList
                    .mapNotNull { it.city?.takeIf { city -> city.isNotBlank() } }
                    .distinct()
                    .sorted()
                _cities.value = cityList
            }
        }
    }

    fun selectCity(city: String) {
        _selectedCity.value = city
    }
}