package com.aqualogicasystem.izsu.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Iron3 : Screen("iron3")
    object Soda : Screen("soda")
    object Chlorine : Screen("chlorine")
    object Profile : Screen("profile")
    object ProfileDetail : Screen("profile_detail")
    object ThemeDemo : Screen("theme_demo")
    object Settings : Screen("settings")
    object Calculator : Screen("calculator")
    object SodaCalculator : Screen("soda_calculator")
    object ChlorineCalculator : Screen("chlorine_calculator/{tab}") {
        fun createRoute(tab: Int = 0) = "chlorine_calculator/$tab"
    }
}
