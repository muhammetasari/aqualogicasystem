package com.aqualogicasystem.izsu.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aqualogicasystem.izsu.ui.screens.main.HomeScreen
import com.aqualogicasystem.izsu.ui.screens.calculation.IronCalculatorScreen
import com.aqualogicasystem.izsu.ui.screens.calculation.ChlorineCalculatorScreen
import com.aqualogicasystem.izsu.ui.screens.calculation.SodaCalculatorScreen

fun NavGraphBuilder.homeNavGraph(navController: NavController) {
    // Home Screen
    composable(route = Screen.Home.route) {
        HomeScreen(navController = navController)
    }

    // Bottom bar'dan erişilen calculator ekranları
    composable(route = Screen.Iron3.route) {
        IronCalculatorScreen(navController = navController)
    }

    composable(route = Screen.Soda.route) {
        SodaCalculatorScreen(navController = navController)
    }

    composable(route = Screen.Chlorine.route) {
        ChlorineCalculatorScreen(navController = navController)
    }

    // Eski route'lar (geriye dönük uyumluluk için)
    composable(route = Screen.Calculator.route) {
        IronCalculatorScreen(navController = navController)
    }

    composable(route = Screen.SodaCalculator.route) {
        SodaCalculatorScreen(navController = navController)
    }

    composable(route = Screen.ChlorineCalculator.route) {
        ChlorineCalculatorScreen(navController = navController)
    }
}
