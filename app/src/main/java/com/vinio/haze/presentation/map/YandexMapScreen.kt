package com.vinio.haze.presentation.map

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vinio.haze.R
import com.vinio.haze.domain.model.Place
import com.vinio.haze.presentation.map.InfoDialog.PoiInfoDialog
import com.vinio.haze.presentation.navigation.Screen
import com.vinio.haze.presentation.screens.BottomNavItem
import com.vinio.haze.startLocation
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.LinearRing
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polygon
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapType
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolygonMapObject
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.BusinessObjectMetadata
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.delay
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.operation.union.CascadedPolygonUnion
import org.locationtech.jts.geom.Polygon as JtsPolygon

val geometryFactory = GeometryFactory()

@Composable
fun YandexMapScreen(
    modifier: Modifier = Modifier,
    viewModel: YandexMapViewModel = hiltViewModel(),
    navController: NavController,
) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()
    val poiItems by viewModel.poiItems.collectAsState()
    val zoom by viewModel.zoomLevel.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val userPlacemarkState = remember { mutableStateOf<PlacemarkMapObject?>(null) }
    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    val poiCollection = remember { mapView.mapWindow.map.mapObjects.addCollection() }

    val fogCollection = remember { mapView.mapWindow.map.mapObjects.addCollection() }
    var fogPolygonObj by remember { mutableStateOf<PolygonMapObject?>(null) }
    val visibleAreas = remember { mutableStateListOf<LinearRing>() }
    val worldOuterRing = remember {
        LinearRing(
            listOf(
                Point(85.0, -180.0),
                Point(85.0, 180.0),
                Point(-85.0, 180.0),
                Point(-85.0, -180.0),
                Point(85.0, -180.0)
            )
        )
    }
    val locationPoints by viewModel.locationPoints.observeAsState(emptyList())
    var isFollowingUser by remember { mutableStateOf(true) }

    val fogOpacity by viewModel.fogOpacity.collectAsState()
    val showPOI by viewModel.showPOI.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f),
            factory = { mapView }
        )

        // --- Первая кнопка с увеличением и фоном ---
        Box(
            modifier = Modifier
                .padding(16.dp, 60.dp, 16.dp, 16.dp)
                .align(Alignment.TopStart)
                .size(64.dp) // увеличиваем размер контейнера 1.5 раза
                .background(
                    color = Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { navController.navigate(BottomNavItem.CityList.route) },
                modifier = Modifier
                    .size(64.dp) // сама кнопка 64.dp, внутри контейнера 96.dp
                    .clip(CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar),
                    contentDescription = "Profile",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // --- Группа из трех кнопок с общим фоном и смещением вниз ---
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .offset(y = 140.dp) // сдвиг вниз на 40.dp, регулируй под нужный отступ
                .background(
                    color = Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = {
                    val currentZoom = mapView.mapWindow.map.cameraPosition.zoom
                    mapView.mapWindow.map.move(
                        CameraPosition(
                            mapView.mapWindow.map.cameraPosition.target,
                            currentZoom + 1f,
                            0f,
                            0f
                        ),
                        Animation(Animation.Type.SMOOTH, 0.3f),
                        null
                    )
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.plus_v),
                        contentDescription = "Zoom In"
                    )
                }
                IconButton(onClick = {
                    val currentZoom = mapView.mapWindow.map.cameraPosition.zoom
                    mapView.mapWindow.map.move(
                        CameraPosition(
                            mapView.mapWindow.map.cameraPosition.target,
                            currentZoom - 1f,
                            0f,
                            0f
                        ),
                        Animation(Animation.Type.SMOOTH, 0.3f),
                        null
                    )
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.minus_v),
                        contentDescription = "Zoom Out"
                    )
                }
                IconButton(onClick = {
                    isFollowingUser = !isFollowingUser
                    userLocation?.let { point ->
                        mapView.mapWindow.map.move(
                            CameraPosition(point, mapView.mapWindow.map.cameraPosition.zoom, 0f, 0f),
                            Animation(Animation.Type.SMOOTH, 1.0f),
                            null
                        )
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.location_v),
                        contentDescription = "Locate",
                        tint = if (isFollowingUser) Color.Blue else Color.Gray
                    )
                }
            }
        }
    }


    LaunchedEffect(Unit) {
        mapView.mapWindow.map.mapType = MapType.MAP
        viewModel.loadLocationPoints()

        val region = mapView.mapWindow.map.visibleRegion
        val bbox = BoundingBox(region.bottomLeft, region.topRight)
        viewModel.requestSearch(bbox)

        fogPolygonObj = fogCollection.addPolygon(
            Polygon(worldOuterRing, visibleAreas.toList())
        ).apply {
            fillColor = 0xF5CCCCCC.toInt()
            strokeColor = 0x00000000.toInt()
            strokeWidth = 0f
            zIndex = 0f
        }
    }

    LaunchedEffect(locationPoints, fogPolygonObj, fogOpacity) {
        if (locationPoints.isNotEmpty()
            && fogPolygonObj != null) {
            val polygons = locationPoints.map { point ->
                makeSquarePolygon(Point(point.cellLat, point.cellLon))
            }

            val union = CascadedPolygonUnion.union(polygons)

            visibleAreas.clear()
            when (union) {
                is JtsPolygon -> visibleAreas.add(fromJtsPolygon(union))
                is org.locationtech.jts.geom.MultiPolygon -> {
                    for (i in 0 until union.numGeometries) {
                        visibleAreas.add(fromJtsPolygon(
                            union.getGeometryN(i) as JtsPolygon
                        ))
                    }
                }
            }

            fogPolygonObj?.fillColor = Color(
                alpha = (fogOpacity / 100f * 0.96f).coerceIn(0f, 1f), // 0.96f для исходной прозрачности 0xF5 в коде
                red = 0.8f, green = 0.8f, blue = 0.8f
            ).toArgb()

            updateFogPolygon(
                fogPolygonObj,
                Polygon(worldOuterRing, visibleAreas.toList())
            )
        }
    }

    LaunchedEffect(poiItems, zoom, showPOI) {
        poiCollection.clear()

        if (!showPOI) return@LaunchedEffect

        val placemarkCollection = poiCollection.addCollection()

        val scale = when {
            zoom >= 17 -> 1.0f
            zoom >= 15 -> 0.8f
            zoom >= 13 -> 0.6f
            else -> 0.4f
        }

        poiItems.forEach { item ->
            val geometryPoint = item.obj?.geometry?.firstOrNull()?.point ?: return@forEach
            val point = Point(geometryPoint.latitude, geometryPoint.longitude)

            val isInVisibleArea = visibleAreas.any { ring ->
                toJtsPolygon(ring.points).contains(
                    geometryFactory.createPoint(
                        Coordinate(
                            point.longitude,
                            point.latitude
                        )
                    )
                )
            }

            val name = item.obj?.name?.toString().orEmpty()

            val metadata = item.obj?.metadataContainer
            val toponymMetadata = metadata?.getItem(ToponymObjectMetadata::class.java)
            val toponymAddress = toponymMetadata?.address?.formattedAddress
            val business = metadata?.getItem(BusinessObjectMetadata::class.java)
            val businessAddress = business?.address?.formattedAddress
            val address = businessAddress ?: toponymAddress
            val cityFromToponym = toponymMetadata?.address?.components
                ?.firstOrNull { it.kinds.contains(Address.Component.Kind.LOCALITY) }
                ?.name

            val cityFromBusiness = business?.address?.components
                ?.firstOrNull { component ->
                    component.kinds.any { kind ->
                        kind == Address.Component.Kind.LOCALITY || kind == Address.Component.Kind.DISTRICT
                    }
                }?.name

            val city = cityFromBusiness ?: cityFromToponym

            Log.d("City", city.toString())
            Log.d("Address", address.toString())

            if (isInVisibleArea) {
                val place = Place(null, name, city, address, null, point.latitude, point.longitude)

                viewModel.savePlaceIfNeeded(place)

                placemarkCollection.addPlacemark().apply {
                    geometry = point
                    setIcon(ImageProvider.fromResource(context, R.drawable.ic_marker))
                    userData = place
                    setIconStyle(IconStyle().apply {
                        this.scale = scale
                        this.anchor?.set(0.5f, 1.0f)
                    })
                }
            } else {
                placemarkCollection.addPlacemark().apply {
                    geometry = point
                    setIcon(ImageProvider.fromResource(context, R.drawable.ic_marker_gray))
                    userData = null
                    setIconStyle(IconStyle().apply {
                        this.scale = scale
                        this.anchor?.set(0.5f, 1.0f)
                    })
                }
            }
        }
        placemarkCollection.addTapListener { mapObject, _ ->
            val tappedPlace = mapObject.userData as? Place ?: return@addTapListener false
            Toast.makeText(context, tappedPlace.name, Toast.LENGTH_SHORT).show()
            selectedPlace = tappedPlace
            true
        }
    }


    selectedPlace?.let { place ->
        PoiInfoDialog(place = place, onDismiss = { selectedPlace = null })
    }

    LaunchedEffect(userLocation) {
        userLocation?.let { point ->
            val placemark = userPlacemarkState.value
            if (placemark == null || !placemark.isValid) {
                val newPlacemark = mapView.mapWindow.map.mapObjects.addPlacemark(point).apply {
                    setIcon(ImageProvider.fromResource(context, R.drawable.ic_user_location))
                    setIconStyle(IconStyle().apply {
                        scale = 1.0f
                        anchor?.set(0.5f, 0.5f)
                    })
                }
                userPlacemarkState.value = newPlacemark
            } else {
                animatePlacemarkMove(placemark, point)
            }

            if (isFollowingUser) {
                mapView.mapWindow.map.move(
                    CameraPosition(point, 16.0f, 0f, 0f),
                    Animation(Animation.Type.SMOOTH, 1.0f),
                    null
                )
            }

            val newPolygon = makeSquarePolygon(point)
            val union =
                CascadedPolygonUnion.union(
                    listOf(newPolygon) + visibleAreas.map { toJtsPolygon(it.points) }
                )

            visibleAreas.clear()
            when (union) {
                is JtsPolygon -> visibleAreas.add(fromJtsPolygon(union))
                is org.locationtech.jts.geom.MultiPolygon -> {
                    for (i in 0 until union.numGeometries) {
                        visibleAreas.add(fromJtsPolygon(
                            union.getGeometryN(i) as JtsPolygon)
                        )
                    }
                }
            }
            updateFogPolygon(
                fogPolygonObj, Polygon(worldOuterRing, visibleAreas.toList())
            )
        }
    }

    DisposableEffect(mapView) {
        (context as? Activity)?.startLocation()
        val listener =
            com.yandex.mapkit.map.CameraListener { map, cameraPosition, update, finished ->
                if (finished) {
                    val zoom = cameraPosition.zoom
                    viewModel.onZoomChanged(zoom)

                    val region = mapView.mapWindow.map.visibleRegion
                    val bbox = BoundingBox(region.bottomLeft, region.topRight)
                    viewModel.requestSearch(bbox)
                }
            }

        mapView.mapWindow.map.addCameraListener(listener)

        onDispose {
            Log.d("MapDebug", "CameraListener was removed")
            mapView.mapWindow.map.removeCameraListener(listener)
        }
    }
}

fun BoundingBox.toFormatString(): String {
    return "southWest: ${this.southWest.latitude}, ${this.southWest.longitude}\n" +
            "northEast: ${this.northEast.latitude}, ${this.northEast.longitude}"
}

private fun lerp(start: Double, end: Double, fraction: Float): Double =
    start + (end - start) * fraction

private suspend fun animatePlacemarkMove(placemark: PlacemarkMapObject, target: Point) {
    if (!placemark.isValid) return
    val start = placemark.geometry
    repeat(20) { step ->
        val t = (step + 1) / 20f
        placemark.geometry = Point(
            lerp(start.latitude, target.latitude, t),
            lerp(start.longitude, target.longitude, t)
        )
        delay(16)
    }
}

fun makeSquarePolygon(center: Point, sideMeters: Double = 300.0): JtsPolygon {
    val half = sideMeters / 2.0
    val dx = half / (40075000 * Math.cos(center.latitude * Math.PI / 180) / 360)
    val dy = half / 111320.0

    val points = listOf(
        Point(center.latitude - dy, center.longitude - dx),
        Point(center.latitude - dy, center.longitude + dx),
        Point(center.latitude + dy, center.longitude + dx),
        Point(center.latitude + dy, center.longitude - dx),
        Point(center.latitude - dy, center.longitude - dx) // замыкаем
    )
    return toJtsPolygon(points)
}

fun toJtsPolygon(points: List<Point>): JtsPolygon {
    val coordinates = points.map { Coordinate(it.longitude, it.latitude) }.toTypedArray()
    val shell = geometryFactory.createLinearRing(coordinates)
    return geometryFactory.createPolygon(shell)
}

fun fromJtsPolygon(jtsPolygon: JtsPolygon): LinearRing {
    return LinearRing(jtsPolygon.exteriorRing.coordinates.map { Point(it.y, it.x) })
}

fun updateFogPolygon(fog: PolygonMapObject?, polygon: Polygon) {
    if (fog == null) return
    fog.geometry = polygon
}

