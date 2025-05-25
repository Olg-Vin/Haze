package com.vinio.haze.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.vinio.haze.R
import com.vinio.haze.presentation.navigation.Screen

@Composable
fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val barHeight = 76.dp
    val circleDiameter = 72.dp

    val currentRoute = navController
        .currentBackStackEntryAsState().value?.destination?.route

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight + circleDiameter / 2)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(barHeight)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(Color.Transparent)
        )

        // Круглая кнопка
        IconButton(
            modifier = Modifier
                .size(circleDiameter)
                .align(Alignment.TopCenter)
                .shadow(6.dp, CircleShape)
                .background(Color.White, CircleShape)
                .zIndex(1f),
            onClick = {
                navController.navigate(Screen.Map.route)
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.map_world),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        HorizontalDivider(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .width(3.dp)
                .padding(top = circleDiameter / 2 - 1.dp)
                .zIndex(-1f),
            thickness = 2.dp,
            color = Color(0xFFE0E0E0)
        )

        // Иконки
        Row(
            Modifier
                .fillMaxWidth()
                .height(barHeight)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedNavIcon(
                iconRes = BottomNavItem.CityList.icon!!,
                isSelected = currentRoute == BottomNavItem.CityList.route,
                onClick = {
                    if (currentRoute != BottomNavItem.CityList.route) {
                        navController.navigate(BottomNavItem.CityList.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )

            AnimatedNavIcon(
                iconRes = BottomNavItem.PoiList.icon!!,
                isSelected = currentRoute == BottomNavItem.PoiList.route,
                onClick = {
                    if (currentRoute != BottomNavItem.PoiList.route) {
                        navController.navigate(BottomNavItem.PoiList.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )


            Spacer(modifier = Modifier.width(circleDiameter - 54.dp))
            VerticalDivider(
                modifier = Modifier
                    .height(28.dp)
                    .width(3.dp),
                color = Color(0xFFE0E0E0)
            )
            Spacer(modifier = Modifier.width(circleDiameter - 54.dp))

            AnimatedNavIcon(
                iconRes = BottomNavItem.Achievements.icon!!,
                isSelected = currentRoute == BottomNavItem.Achievements.route,
                onClick = {
                    if (currentRoute != BottomNavItem.Achievements.route) {
                        navController.navigate(BottomNavItem.Achievements.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )

            AnimatedNavIcon(
                iconRes = BottomNavItem.Settings.icon!!,
                isSelected = currentRoute == BottomNavItem.Settings.route,
                onClick = {
                    if (currentRoute != BottomNavItem.Settings.route) {
                        navController.navigate(BottomNavItem.Settings.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
