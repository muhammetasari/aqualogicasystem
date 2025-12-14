package com.aqualogicasystem.izsu.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aqualogicasystem.izsu.ui.screens.CalculatorScreen
import com.aqualogicasystem.izsu.ui.screens.HomeScreen

fun NavGraphBuilder.homeNavGraph(navController: NavController) {
    composable(Screen.Home.route) {
        HomeScreen(navController = navController)
    }
    composable(Screen.Calculator.route) {
        CalculatorScreen(navController = navController)
    }
}
