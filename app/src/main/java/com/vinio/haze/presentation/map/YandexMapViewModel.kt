package com.vinio.haze.presentation.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class YandexMapViewModel : ViewModel() {
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

    fun onZoomChanged(zoom: Float) {
        _zoomLevel.value = zoom
    }

    init {
        viewModelScope.launch {
            _boundingBoxFlow
                .debounce(500)
                .collect { bbox -> searchInViewport(bbox) }
        }
    }

    fun requestSearch(bbox: BoundingBox) {
        Log.d("Search", "Requesting search with bbox: ${bbox.toFormatString()}")
        viewModelScope.launch {
            _boundingBoxFlow.emit(bbox)
        }
    }

    private fun searchInViewport(boundingBox: BoundingBox) {
        val key = boundingBox.key()

        // Если уже искали — используем кеш
        searchCache[key]?.let { cached ->
            Log.d("Search", "Using cached result for $key")
            _poiItems.value = cached
            return
        }

        Log.d("Search", "Ищем POI в bbox: ${boundingBox.toFormatString()}")
        currentSession?.cancel()

        val options = SearchOptions().apply {
            searchTypes = SearchType.BIZ.value
            resultPageSize = 50
        }

        currentSession = searchManager.submit(
            "достопримечательности",
            Geometry.fromBoundingBox(boundingBox),
            options,
            object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    Log.d("SearchDebug", "response: ${response.collection.children.size}")
                    _poiItems.value = response.collection.children
                    Log.d("SearchDebug", "${_poiItems.value}")
                }

                override fun onSearchError(error: Error) {
                    Log.e("Search", "Ошибка поиска: $error")
                }
            }
        )
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
