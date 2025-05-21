package com.vinio.haze.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vinio.haze.presentation.map.YandexMapScreen
import com.vinio.haze.presentation.startScreen.StartScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.Check.route) {
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

        /*composable(Screen.CityList.route) {
            CityListScreen()
        }*/
    }
}

