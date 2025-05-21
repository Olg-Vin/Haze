package com.vinio.haze.presentation.navigation

import androidx.annotation.DrawableRes
import com.vinio.haze.R

sealed class Screen(val route: String) {
    data object Check : Screen("check")
    data object Start : Screen("start")
    data object Map : Screen("map")
    object BottomNav : Screen("bottom_nav")
}


sealed class BottomNavItem(val route: String, val label: String, @DrawableRes val icon: Int? = null) {
    object CityList : BottomNavItem("cities", "Города", R.drawable.ic_cities)
    object PoiList : BottomNavItem("poi", "Места", R.drawable.ic_poi)
    object Achievements : BottomNavItem("achievements", "Достижения", R.drawable.ic_achievements)
    object Settings : BottomNavItem("settings", "Настройки", R.drawable.ic_settings)
}