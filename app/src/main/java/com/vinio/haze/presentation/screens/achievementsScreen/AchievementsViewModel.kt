package com.vinio.haze.presentation.screens.achievementsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinio.haze.domain.repository.PlaceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val repository: PlaceRepository
) : ViewModel() {
    val currentCount: StateFlow<Int> = repository.getOpenedPoiCount()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)
}