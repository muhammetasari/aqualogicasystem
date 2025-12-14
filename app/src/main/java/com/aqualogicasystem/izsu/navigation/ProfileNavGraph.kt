package com.aqualogicasystem.izsu.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aqualogicasystem.izsu.ui.screens.main.ProfileScreen
import com.aqualogicasystem.izsu.ui.theme.demo.ThemeDemoScreen
import com.aqualogicasystem.izsu.ui.screens.main.SettingsScreen

fun NavGraphBuilder.profileNavGraph(
    navController: NavController,
    onLogout: () -> Unit
) {
    composable(Screen.Profile.route) {
        ProfileScreen(
            navController = navController,
            onLogout = onLogout,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
        )
    }

    composable(Screen.ThemeDemo.route) {
        ThemeDemoScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(Screen.Settings.route) {
        SettingsScreen(
            navController = navController
        )
    }
}
