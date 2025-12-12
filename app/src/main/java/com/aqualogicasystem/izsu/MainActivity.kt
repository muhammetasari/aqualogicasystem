package com.aqualogicasystem.izsu

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aqualogicasystem.izsu.utils.HandleAppExit
import com.aqualogicasystem.izsu.navigation.Screen
import com.aqualogicasystem.izsu.navigation.authNavGraph
import com.aqualogicasystem.izsu.navigation.homeNavGraph
import com.aqualogicasystem.izsu.navigation.profileNavGraph
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme
import com.aqualogicasystem.izsu.ui.viewmodel.AuthViewModel
import com.aqualogicasystem.izsu.ui.viewmodel.ThemeViewModel
import com.aqualogicasystem.izsu.ui.viewmodel.ThemeViewModelFactory
import com.aqualogicasystem.izsu.utils.LocaleHelper

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.setLocale(it, LocaleHelper.getPersistedLocale(it)) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Initialize ThemeViewModel to observe theme changes
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ThemeViewModelFactory(application)
            )
            val themeConfig by themeViewModel.themeConfig.collectAsState()

            IzsuAppTheme(themeConfig = themeConfig) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    IzsuNavigation()
                }
            }
        }
    }
}

@Composable
fun IzsuNavigation() {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = viewModel()
    val authState by viewModel.authState.collectAsState()

    // Mevcut route'u izle
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Sadece Home ekranında çıkış dialog'u göster
    if (currentRoute == Screen.Home.route) {
        HandleAppExit(
            exitTitle = androidx.compose.ui.res.stringResource(id = R.string.exit_app_title),
            exitMessage = androidx.compose.ui.res.stringResource(id = R.string.exit_app_message),
            yesText = androidx.compose.ui.res.stringResource(id = R.string.yes),
            noText = androidx.compose.ui.res.stringResource(id = R.string.no)
        )
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        authNavGraph(
            navController = navController,
            viewModel = viewModel
        )
        homeNavGraph(navController = navController)
        profileNavGraph(
            navController = navController,
            onLogout = {
                viewModel.signOut()
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
        )
    }
}
