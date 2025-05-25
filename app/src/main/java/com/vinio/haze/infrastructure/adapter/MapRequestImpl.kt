package com.vinio.haze.infrastructure.adapter

import com.vinio.haze.domain.adapter.MapRequest
import com.yandex.mapkit.GeoObjectCollection.Item
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.search.Response
import com.yandex.runtime.Error
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MapRequestImpl @Inject constructor() : MapRequest {

    private val searchManager =
        SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)

    override suspend fun fetchPois(bbox: BoundingBox): List<Item> = suspendCoroutine { cont ->
        val options = SearchOptions().apply {
            searchTypes = SearchType.BIZ.value
            resultPageSize = 50
        }

        val session = searchManager.submit(
            "достопримечательности",
            Geometry.fromBoundingBox(bbox),
            options,
            object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    cont.resume(response.collection.children)
                }

                override fun onSearchError(error: Error) {
                    cont.resumeWithException(RuntimeException("Ошибка поиска: $error"))
                }
            }
        )
    }
}