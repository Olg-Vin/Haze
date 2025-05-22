package com.vinio.haze.presentation.screens.poiScreens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vinio.haze.R
import com.vinio.haze.presentation.navigation.Screen
import com.vinio.haze.presentation.screens.BottomNavItem


@Composable
fun PoiListScreen(
    navController: NavController,
    viewModel: PoiListViewModel = hiltViewModel(),
) {
    val places by viewModel.places.collectAsState()
    val cities by viewModel.cities.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                "Достопримечательности",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp)
            Spacer(
                modifier = Modifier.height(8.dp)
            )

            SimpleCityDropdown(
                cities = cities,
                selectedCity = selectedCity,
                onCitySelected = { viewModel.selectCity(it) }
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            val filteredPlaces = if (selectedCity.isEmpty()) {
                places
            } else {
                places.filter { it.city == selectedCity }
            }

            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                items(filteredPlaces) { place ->
                    PoiItem(
                        name = place.name,
                        imageRes = R.drawable.ic_poi,
                        onClick = {
                            navController.navigate(
                                BottomNavItem.PoiDetails.createRoute(place.id!!, isCity = false)
                            )
                        }
                    )
                }
            }
        }

        IconButton(
            onClick = { navController.navigate(Screen.Map.route) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Открыть карту",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun PoiItem(name: String, @DrawableRes imageRes: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = name,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(name, fontSize = 18.sp)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
    }
}