package com.aqualogicasystem.izsu.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aqualogicasystem.izsu.ui.components.IzsuLogo
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2750)
        onNavigateToLogin()
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        IzsuLogo(size = 240.dp)
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    IzsuAppTheme {
        SplashScreen(onNavigateToLogin = {})
    }
}
