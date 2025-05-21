package com.vinio.haze.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vinio.haze.presentation.navigation.BottomNavItem
import com.vinio.haze.presentation.screens.HomeScreen
import com.vinio.haze.presentation.screens.ProfileScreen
import com.vinio.haze.presentation.screens.SearchScreen
import com.vinio.haze.presentation.screens.SettingsScreen

@Composable
fun BottomNavScreen() {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(bottomNavController) }
    ) { innerPadding ->
        NavHost(
            bottomNavController,
            startDestination = BottomNavItem.CityList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.CityList.route) { HomeScreen() }
            composable(BottomNavItem.PoiList.route) { SearchScreen() }
            composable(BottomNavItem.Achievements.route) { ProfileScreen() }
            composable(BottomNavItem.Settings.route) { SettingsScreen() }
        }
    }
}

