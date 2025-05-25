package com.vinio.haze.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItemDefaults.containerColor
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vinio.haze.presentation.map.YandexMapScreen
import com.vinio.haze.presentation.screens.BottomNavBar
import com.vinio.haze.presentation.screens.BottomNavItem
import com.vinio.haze.presentation.screens.achievementsScreen.AchievementsScreen
import com.vinio.haze.presentation.screens.cityScreens.CityListScreen
import com.vinio.haze.presentation.screens.cityScreens.cityDetails.CityDetailsScreen
import com.vinio.haze.presentation.screens.poiScreens.PoiListScreen
import com.vinio.haze.presentation.screens.poiScreens.poiDetails.PoiDetailsScreen
import com.vinio.haze.presentation.screens.settingsScreen.SettingsScreen
import com.vinio.haze.presentation.startScreen.StartScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val bottomNavRoutes = listOf(
        BottomNavItem.CityList.route,
        BottomNavItem.PoiList.route,
        BottomNavItem.Achievements.route,
        BottomNavItem.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                BottomNavBar(navController = navController)
            }
        },
        containerColor = Color.White,
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Check.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Экраны без BottomNav
            composable(Screen.Check.route) {
                PermissionCheckScreen(
                    onPermissionsGranted = {
                        navController.navigate(Screen.Map.route) {
                            popUpTo(Screen.Check.route) { inclusive = true }
                        }
                    },
                    onPermissionsNotGranted = {
                        navController.navigate(Screen.Start.route) {
                            popUpTo(Screen.Check.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Start.route) {
                StartScreen(
                    onPermissionsGranted = {
                        navController.navigate(Screen.Map.route) {
                            popUpTo(Screen.Start.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Map.route) {
                YandexMapScreen(navController = navController)
            }

            // Экраны BottomNav
            composable(BottomNavItem.CityList.route) {
                CityListScreen(navController = navController)
            }

            composable(BottomNavItem.PoiList.route) {
                PoiListScreen(navController = navController)
            }

            composable(BottomNavItem.Achievements.route) {
                AchievementsScreen(navController = navController)
            }

            composable(BottomNavItem.Settings.route) {
                SettingsScreen()
            }

            // Детали POI
            composable(
                route = "poiDetails/{poiId}?isCity={isCity}",
                arguments = listOf(
                    navArgument("poiId") { type = NavType.StringType },
                    navArgument("isCity") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { backStackEntry ->
                val poiId = backStackEntry.arguments?.getString("poiId") ?: return@composable
                val isCity = backStackEntry.arguments?.getBoolean("isCity") ?: false

                PoiDetailsScreen(
                    poiId = poiId,
                    isCityMode = isCity,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = "cityDetails/{cityName}",
                arguments = listOf(
                    navArgument("cityName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val cityName = backStackEntry.arguments?.getString("cityName") ?: return@composable

                CityDetailsScreen(
                    cityName = cityName,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}


