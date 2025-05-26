package com.vinio.haze.presentation.screens.settingsScreen

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinio.haze.application.useCases.SettingsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsUseCases: SettingsUseCases
) : ViewModel() {

    private val _avatarUri = MutableStateFlow<Uri?>(null)
    val avatarUri: StateFlow<Uri?> = _avatarUri

    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username

    private val _fogOpacity = MutableStateFlow(50f)
    val fogOpacity: StateFlow<Float> = _fogOpacity

    private val _showPOI = MutableStateFlow(true)
    val showPOI: StateFlow<Boolean> = _showPOI

    private val _fogColor = MutableStateFlow(Color(0xFF9575CD))
    val fogColor: StateFlow<Color> = _fogColor

    private val _notifyAchievements = MutableStateFlow(true)
    val notifyAchievements: StateFlow<Boolean> = _notifyAchievements

    init {
        viewModelScope.launch {
            launch {
                settingsUseCases.getAvatarUriFlow.invoke()
                    .collectLatest { _avatarUri.value = it?.let(Uri::parse) }
            }
            launch {
                settingsUseCases.getUsernameFlow.invoke().collectLatest { _username.value = it }
            }
            launch {
                settingsUseCases.getFogOpacityFlow.invoke().collectLatest { _fogOpacity.value = it }
            }
            launch {
                settingsUseCases.getShowPOIFlow.invoke().collectLatest { _showPOI.value = it }
            }
            launch {
                settingsUseCases.getFogColorFlow.invoke().collectLatest { _fogColor.value = it }
            }
            launch {
                settingsUseCases.getNotifyAchievementsFlow.invoke()
                    .collectLatest { _notifyAchievements.value = it }
            }
        }
    }

    fun setFogColor(color: Color) {
        _fogColor.value = color
        viewModelScope.launch {
            settingsUseCases.saveFogColor.invoke(color)
        }
    }

    fun setNotifyAchievements(enabled: Boolean) {
        _notifyAchievements.value = enabled
        viewModelScope.launch {
            settingsUseCases.saveNotifyAchievements.invoke(enabled)
        }
    }

    fun setAvatarUri(uri: Uri) {
        _avatarUri.value = uri
        viewModelScope.launch {
            settingsUseCases.saveAvatarUri.invoke(uri.toString())
        }
    }

    fun setUsername(name: String) {
        _username.value = name
        viewModelScope.launch {
            settingsUseCases.saveUsername.invoke(name)
        }
    }

    fun setFogOpacity(value: Float) {
        _fogOpacity.value = value
        viewModelScope.launch {
            settingsUseCases.saveFogOpacity.invoke(value)
        }
    }

    fun setShowPOI(show: Boolean) {
        _showPOI.value = show
        viewModelScope.launch {
            settingsUseCases.saveShowPOI.invoke(show)
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            settingsUseCases.clearSettings.invoke()
            _avatarUri.value = null
            _username.value = ""
            _fogOpacity.value = 50f
            _showPOI.value = true
        }
    }
}

