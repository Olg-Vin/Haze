package com.vinio.haze.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vinio.haze.presentation.screens.cityScreens.CityListScreen
import com.vinio.haze.presentation.screens.poiScreens.PoiListScreen
import com.vinio.haze.presentation.screens.poiScreens.poiDetails.PoiDetailsScreen

@Composable
fun BottomNavScreen(
    navController: NavController
) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(bottomNavController) }
    ) { innerPadding ->
        NavHost(
            bottomNavController,
            startDestination = BottomNavItem.CityList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.CityList.route) { CityListScreen() }
            composable(BottomNavItem.PoiList.route) {
                PoiListScreen(
                    navController = navController
                )
            }
            composable(BottomNavItem.Achievements.route) { ProfileScreen() }
            composable(BottomNavItem.Settings.route) { SettingsScreen() }
            /*composable(
                route = "poiDetails/{poiId}?isCity={isCity}",
                arguments = listOf(
                    navArgument("poiId") { type = NavType.StringType },
                    navArgument("isCity") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) {
                val poiId = it.arguments?.getString("poiId") ?: return@composable
                val isCity = it.arguments?.getBoolean("isCity") ?: false
                PoiDetailsScreen(poiId = poiId, isCityMode = isCity)
            }*/
        }
    }
}

