package com.vinio.haze

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error

fun makeSearch() {
    val searchManager = SearchFactory.getInstance().createSearchManager(
        SearchManagerType.ONLINE
    )
    val point = Geometry.fromPoint(Point(59.95, 30.32))
    val searchSession = searchManager.submit("кафе", point, SearchOptions(),
        object: Session.SearchListener {
            override fun onSearchError(p0: Error) { Log.d("Search","Error: $p0") }
            @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
            override fun onSearchResponse(p0: Response) {
                Log.d("Search","Success ${p0.collection.children.size}")
                for (r in p0.collection.children){
                    Log.d("Search","Success ${r.obj?.name}")
                }
            }
        }
    )
}