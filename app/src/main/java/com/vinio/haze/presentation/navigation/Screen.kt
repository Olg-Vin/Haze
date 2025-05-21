package com.vinio.haze.presentation.navigation

sealed class Screen(val route: String) {
    data object Check : Screen("check")
    data object Start : Screen("start")
    data object Map : Screen("map")
    data object CityList : Screen("city_list")
}
