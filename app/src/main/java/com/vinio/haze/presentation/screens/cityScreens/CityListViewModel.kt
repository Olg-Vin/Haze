package com.vinio.haze.presentation.screens.cityScreens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinio.haze.domain.repository.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CityListViewModel @Inject constructor(
    private val repository: PlaceRepository
) : ViewModel() {
    private val _cities = MutableStateFlow<List<String>>(emptyList())
    val cities: StateFlow<List<String>> = _cities

    init {
        viewModelScope.launch {
            repository.getCities()
                .collect { cityList ->
                    _cities.value = cityList
                }
        }
    }
}
