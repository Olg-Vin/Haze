package com.vinio.haze.presentation.map

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinio.haze.application.useCases.FetchPoiUseCase
import com.vinio.haze.application.useCases.GetCityByLocationUseCase
import com.vinio.haze.application.useCases.GetUserProfileUseCase
import com.vinio.haze.application.useCases.ObserveLocationEnabledUseCase
import com.vinio.haze.application.useCases.ProcessPoiItemUseCase
import com.vinio.haze.application.useCases.SettingsUseCases
import com.vinio.haze.diAndUtils.LocationUtil
import com.vinio.haze.diAndUtils.NetworkUtil
import com.vinio.haze.domain.location.LocationRepository
import com.vinio.haze.domain.model.LocationPoint
import com.vinio.haze.domain.model.Place
import com.vinio.haze.domain.repository.LocationPointRepository
import com.yandex.mapkit.GeoObjectCollection.Item
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class YandexMapViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationRepository: LocationRepository,
    private val repository: LocationPointRepository,
    private val fetchPoiUseCase: FetchPoiUseCase,
    private val settingsUseCases: SettingsUseCases,
    private val processPoiItemUseCase: ProcessPoiItemUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getCityByLocationUseCase: GetCityByLocationUseCase,
    private val observeLocationEnabledUseCase: ObserveLocationEnabledUseCase,
) : ViewModel() {

    private val _poiItems = MutableStateFlow<List<Item>>(emptyList())
    val poiItems: StateFlow<List<Item>> = _poiItems

    private val _boundingBoxFlow = MutableSharedFlow<BoundingBox>()

    //  TODO оптимизация под прод при помощи Map<String, Pair<timestamp, List<Item>>>
    private val searchCache = mutableMapOf<String, List<Item>>()

    private val _zoomLevel = MutableStateFlow(17.0f)
    val zoomLevel: StateFlow<Float> = _zoomLevel

    private val _userLocation = MutableStateFlow<Point?>(null)
    val userLocation: StateFlow<Point?> = _userLocation

    private val _locationPoints = MutableLiveData<List<LocationPoint>>(emptyList())
    val locationPoints: LiveData<List<LocationPoint>> = _locationPoints

    private val _avatarUri = MutableStateFlow<String?>(null)
    val avatarUri: StateFlow<String?> = _avatarUri.asStateFlow()

    private val _userLevel = MutableStateFlow(1)
    val userLevel: StateFlow<Int> = _userLevel.asStateFlow()

    private val _currentCity = MutableStateFlow("...")
    val currentCity: StateFlow<String> = _currentCity

    private val _fogColor = MutableStateFlow(Color(0xFF9575CD))
    val fogColor: StateFlow<Color> = _fogColor

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadLocationPoints() {
        viewModelScope.launch {
            val pointsFromDb = repository.getAllLocationPoints()
            _locationPoints.postValue(pointsFromDb)
        }

        viewModelScope.launch {
            settingsUseCases.getFogColorFlow().collectLatest {
                Log.d("Color", "load color ${it.toArgb()}")
                _fogColor.value = it
            }
        }
    }

    fun onZoomChanged(zoom: Float) {
        _zoomLevel.value = zoom
    }

    private val _fogOpacity = MutableStateFlow(50f)
    val fogOpacity: StateFlow<Float> = _fogOpacity

    private val _showPOI = MutableStateFlow(true)
    val showPOI: StateFlow<Boolean> = _showPOI

    init {
        viewModelScope.launch {
            settingsUseCases.getFogOpacityFlow().collectLatest {
                _fogOpacity.value = it
            }
        }
        viewModelScope.launch {
            settingsUseCases.getShowPOIFlow().collectLatest {
                _showPOI.value = it
            }
        }

        viewModelScope.launch {
            _boundingBoxFlow
                .debounce(500)
                .collect { bbox ->
                    Log.d("Search", "handlePoiSearch запускается с bbox: $bbox")
                    handlePoiSearch(bbox)
                }
        }

        viewModelScope.launch {
            getUserProfileUseCase.getAvatarUri().collect { uri ->
                _avatarUri.value = uri
            }
        }
        viewModelScope.launch {
            getUserProfileUseCase.getUserLevel().collect { level ->
                _userLevel.value = level
            }
        }

        viewModelScope.launch {
            observeLocationEnabledUseCase.execute()
                .collect { enabled ->
                    Log.d("LocationReceiver", "Collected location enabled = $enabled")
                    if (!enabled) {
                        _errorMessage.value = "Геолокация отключена. Включите доступ к местоположению"
                    } else {
                        _errorMessage.value = null
                    }
                }
        }

        viewModelScope.launch {
            observeLocationEnabledUseCase.execute()
                .filter { it }
                .flatMapLatest {
                    locationRepository.locationFlow
                        .filterNotNull()
                        .distinctUntilChangedBy { it.latitude to it.longitude }
                }
                .collect { location ->
                    val point = Point(location.latitude, location.longitude)
                    _userLocation.value = point
                    val city = getCityByLocationUseCase.getCityName(point.latitude, point.longitude)
                    _currentCity.value = city ?: "Неизвестно"
                }
        }
    }

    fun requestSearch(bbox: BoundingBox) {
        Log.d("Search", "Requesting search with bbox: ${bbox.toFormatString()}")
        viewModelScope.launch {
            _boundingBoxFlow.emit(bbox)
        }
    }

    private suspend fun handlePoiSearch(bbox: BoundingBox) {
        if (!NetworkUtil.isNetworkAvailable(context)) {
            Log.e("POI", "Нет подключения к интернету")
            _errorMessage.value = "Нет подключения к интернету"
            return
        }

        val key = bbox.key()
        if (searchCache.containsKey(key)) {
            _poiItems.value = searchCache[key]!!
            return
        }

        val result = fetchPoiUseCase.execute(bbox)
        result.onSuccess { items ->
            searchCache[key] = items
            _poiItems.value = items
        }.onFailure { e ->
            Log.e("POI", "Ошибка получения POI: ${e.message}", e)
            _errorMessage.value = "Ошибка загрузки POI: ${e.message}"
        }
    }

    private fun BoundingBox.key(): String {
        fun Point.round() = Point(
            (latitude * 1000).toInt() / 1000.0,
            (longitude * 1000).toInt() / 1000.0
        )

        val bl = southWest.round()
        val tr = northEast.round()
        return "${bl.latitude},${bl.longitude}_${tr.latitude},${tr.longitude}"
    }

    fun processPoiItem(
        item: Item,
        visibleRings: List<LinearRing>,
        onPlaceReady: (Place?, Point) -> Unit
    ) {
        viewModelScope.launch {
            val place = processPoiItemUseCase.execute(item, visibleRings)
            val point = item.obj?.geometry?.firstOrNull()?.point?.let {
                Point(it.latitude, it.longitude)
            } ?: return@launch

            onPlaceReady(place, point)
        }
    }
}
