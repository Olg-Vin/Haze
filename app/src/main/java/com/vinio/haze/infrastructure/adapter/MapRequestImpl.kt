package com.vinio.haze.infrastructure.adapter

import android.util.Log
import com.vinio.haze.domain.adapter.MapRequest
import com.yandex.mapkit.GeoObjectCollection.Item
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MapRequestImpl @Inject constructor() : MapRequest {

    private val searchManager = SearchFactory.getInstance()
            .createSearchManager(SearchManagerType.ONLINE)
    private var currentSession: Session? = null

    override suspend fun fetchPois(
        bbox: BoundingBox
    ): Result<List<Item>> = suspendCoroutine { cont ->
        try {
            val options = SearchOptions().apply {
                searchTypes = SearchType.BIZ.value
                resultPageSize = 50
            }

            Log.d("POI", "Отправка запроса к Yandex Search")
            currentSession = searchManager.submit(
                "достопримечательности",
                Geometry.fromBoundingBox(bbox),
                options,
                object : Session.SearchListener {
                    override fun onSearchResponse(response: Response) {
                        Log.d("POI", "Получен ответ от Yandex: ${response.collection.children.size}")
                        cont.resume(Result.success(response.collection.children))
                    }

                    override fun onSearchError(error: Error) {
                        Log.e("POI", "Ошибка поиска: $error")
                        cont.resume(Result.failure(RuntimeException("Ошибка поиска: $error")))
                        currentSession = null
                    }
                }
            )
        } catch (e: Exception) {
            cont.resume(Result.failure(e))
        }
    }
}