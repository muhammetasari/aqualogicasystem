package com.aqualogicasystem.izsu.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.GasMeter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Iron
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.twotone.HeatPump
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import com.aqualogicasystem.izsu.R
import com.aqualogicasystem.izsu.navigation.Screen

@Composable
fun StandardBottomBar(navController: NavController) {
    // Mevcut route'u izle
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Mevcut route'a göre seçili item'ı belirle
    val selectedItem = when (currentRoute) {
        Screen.Home.route -> 0
        Screen.Iron3.route, Screen.Calculator.route -> 1
        Screen.Soda.route, Screen.SodaCalculator.route -> 2
        Screen.Chlorine.route, Screen.ChlorineCalculator.route -> 3
        Screen.Profile.route -> 5
        else -> 0 // Varsayılan olarak Home seçili
    }

    val items = listOf(
        stringResource(id = R.string.bottom_bar_home),
        stringResource(id = R.string.bottom_bar_iron3),
        stringResource(id = R.string.bottom_bar_soda),
        stringResource(id = R.string.bottom_bar_chlorine),
        stringResource(id = R.string.bottom_bar_chemical_settings),
        stringResource(id = R.string.bottom_bar_profile)
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    when (index) {
                        0 -> Icon(Icons.Default.Home, contentDescription = item)
                        1 -> Icon(Icons.TwoTone.HeatPump, contentDescription = item)
                        2 -> Icon(Icons.Default.WaterDrop, contentDescription = item)
                        3 -> Icon(Icons.Default.GasMeter, contentDescription = item)
                        4 -> Icon(Icons.Default.Science, contentDescription = item)
                        5 -> Icon(Icons.Default.AccountCircle, contentDescription = item)
                    }
                },
                label = {Text(item)},
                selected = selectedItem == index,
                onClick = {
                    when(index) {
                        0 -> navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                        1 -> navController.navigate(Screen.Iron3.route) {
                            launchSingleTop = true
                        }
                        2 -> navController.navigate(Screen.Soda.route) {
                            launchSingleTop = true
                        }
                        3 -> navController.navigate(Screen.Chlorine.route) {
                            launchSingleTop = true
                        }
                        4 -> navController.navigate(Screen.ChemicalSettings.route) {
                            launchSingleTop = true
                        }
                        5 -> navController.navigate(Screen.Profile.route) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StandardBottomBarPreview() {
    StandardBottomBar(navController = rememberNavController())
}
