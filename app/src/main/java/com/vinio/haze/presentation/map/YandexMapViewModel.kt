package com.vinio.haze.presentation.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinio.haze.application.useCases.FetchPoiUseCase
import com.vinio.haze.domain.location.LocationRepository
import com.vinio.haze.domain.model.LocationPoint
import com.vinio.haze.domain.model.Place
import com.vinio.haze.domain.repository.LocationPointRepository
import com.vinio.haze.domain.repository.PlaceRepository
import com.vinio.haze.presentation.screens.settingsScreen.SettingsPreferences
import com.yandex.mapkit.GeoObjectCollection.Item
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.onSuccess

@OptIn(FlowPreview::class)
@HiltViewModel
class YandexMapViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val repository: LocationPointRepository,
    private val fetchPoiUseCase: FetchPoiUseCase,
    private val settingsPreferences: SettingsPreferences,
) : ViewModel() {
    private val searchManager =
        SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)

    private var currentSession: Session? = null

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

    fun loadLocationPoints() {
        viewModelScope.launch {
            val pointsFromDb = repository.getAllLocationPoints()
            _locationPoints.postValue(pointsFromDb)
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
            settingsPreferences.fogOpacityFlow.collect {
                _fogOpacity.value = it
            }
        }
        viewModelScope.launch {
            settingsPreferences.showPOIFlow.collect {
                _showPOI.value = it
            }
        }

        viewModelScope.launch {
            _boundingBoxFlow
                .debounce(500)
                .collect { bbox -> handlePoiSearch(bbox) }
        }

        viewModelScope.launch {
            try {
                locationRepository.locationFlow
                    .filterNotNull()
                    .collect { location ->
                        val point = Point(location.latitude, location.longitude)
                        Log.d("ViewModelDebug", "New user location: ${point.latitude}, ${point.longitude}")
                        _userLocation.value = point
                    }
            } catch (e: Exception) {
                Log.e("ViewModelDebug", "Location collect failed: ${e.message}", e)
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
            Log.e("POI", "Failed to fetch POI: ${e.message}", e)
        }
    }

    suspend fun trySavePlace(place: Place) {
        fetchPoiUseCase.savePlaceIfNotExists(place)
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
}
