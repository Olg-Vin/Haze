package com.vinio.haze.presentation.screens.settingsScreen

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val prefs = SettingsPreferences(context)

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
            launch { prefs.avatarUriFlow.collectLatest { _avatarUri.value = it?.let(Uri::parse) } }
            launch { prefs.usernameFlow.collectLatest { _username.value = it } }
            launch { prefs.fogOpacityFlow.collectLatest { _fogOpacity.value = it } }
            launch { prefs.showPOIFlow.collectLatest { _showPOI.value = it } }
            launch { prefs.fogColorFlow.collectLatest { _fogColor.value = it } }
            launch { prefs.notifyAchievementsFlow.collectLatest { _notifyAchievements.value = it } }
        }
    }

    fun setFogColor(color: Color) {
        _fogColor.value = color
        viewModelScope.launch {
            prefs.saveFogColor(color)
        }
    }

    fun setNotifyAchievements(enabled: Boolean) {
        _notifyAchievements.value = enabled
        viewModelScope.launch {
            prefs.saveNotifyAchievements(enabled)
        }
    }

    fun setAvatarUri(uri: Uri) {
        _avatarUri.value = uri
        viewModelScope.launch {
            prefs.saveAvatarUri(uri.toString())
        }
    }

    fun setUsername(name: String) {
        _username.value = name
        viewModelScope.launch {
            prefs.saveUsername(name)
        }
    }

    fun setFogOpacity(value: Float) {
        _fogOpacity.value = value
        viewModelScope.launch {
            prefs.saveFogOpacity(value)
        }
    }

    fun setShowPOI(show: Boolean) {
        _showPOI.value = show
        viewModelScope.launch {
            prefs.saveShowPOI(show)
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            prefs.clearAll()
            _avatarUri.value = null
            _username.value = ""
            _fogOpacity.value = 50f
            _showPOI.value = true
        }
    }
}

