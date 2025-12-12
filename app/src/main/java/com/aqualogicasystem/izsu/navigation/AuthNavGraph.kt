package com.aqualogicasystem.izsu.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.aqualogicasystem.izsu.ui.screens.SplashScreen
import com.aqualogicasystem.izsu.ui.screens.authscreen.ForgotPasswordScreen
import com.aqualogicasystem.izsu.ui.screens.authscreen.LoginScreen
import com.aqualogicasystem.izsu.ui.screens.authscreen.RegisterScreen
import com.aqualogicasystem.izsu.ui.viewmodel.AuthViewModel

fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    viewModel: AuthViewModel
) {
    composable(Screen.Splash.route) {
        val authState by viewModel.authState.collectAsState()

        SplashScreen(
            onNavigateToLogin = {
                if (authState.currentUser != null) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
        )
    }
    composable(Screen.Login.route) {
        LoginScreen(
            navController = navController,
            viewModel = viewModel,
            onNavigateToForgotPassword = {
                navController.navigate(Screen.ForgotPassword.route)
            },
            onNavigateToRegister = {
                navController.navigate(Screen.Register.route)
            },
            onLoginSuccess = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        )
    }
    composable(Screen.ForgotPassword.route) {
        ForgotPasswordScreen(
            navController = navController,
            viewModel = viewModel,
            onBackPressed = { navController.navigateUp() },
            onResetSuccess = { navController.navigateUp() }
        )
    }
    composable(Screen.Register.route) {
        RegisterScreen(
            navController = navController,
            viewModel = viewModel,
            onBackPressed = { navController.navigateUp() },
            onNavigateToLogin = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            },
            onRegisterSuccess = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Register.route) { inclusive = true }
                }
            }
        )
    }
}
