package com.vinio.haze.presentation.screens

import androidx.annotation.DrawableRes
import com.vinio.haze.R

sealed class BottomNavItem(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int? = null
) {
    object CityList : BottomNavItem("cities", "Города", R.drawable.ic_cities)
    object PoiList : BottomNavItem("poi", "Места", R.drawable.ic_poi)
    object Achievements : BottomNavItem("achievements", "Достижения", R.drawable.ic_achievements)
    object Settings : BottomNavItem("settings", "Настройки", R.drawable.ic_settings)

    object PoiDetails : BottomNavItem("poiDetails/{poiId}?isCity={isCity}", "") {
        fun createRoute(poiId: String, isCity: Boolean = false): String {
            return "poiDetails/$poiId?isCity=$isCity"
        }
    }
}