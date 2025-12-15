package com.aqualogicasystem.izsu.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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

    composable(route = Screen.SodaCalculator.route) {
        SodaCalculatorScreen(navController = navController)
    }

    composable(
        route = Screen.ChlorineCalculator.route,
        arguments = listOf(
            navArgument("tab") {
                type = NavType.IntType
                defaultValue = 0
            }
        )
    ) { backStackEntry ->
        val tab = backStackEntry.arguments?.getInt("tab") ?: 0
        ChlorineCalculatorScreen(
            navController = navController,
            initialTab = tab
        )
    }
}
