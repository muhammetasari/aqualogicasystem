package com.aqualogicasystem.izsu.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardLayout(
    navController: NavController,
    title: String? = null,
    showTopBar: Boolean = true,
    showBackButton: Boolean = true,
    showBottomBar: Boolean = true,
    onNavigateBack: () -> Unit = { navController.popBackStack() },
    topAppBarActions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            if (showTopBar) {
                StandartTopAppBar(
                    title = title ?: "",
                    showBackButton = showBackButton,
                    onNavigateBack = onNavigateBack,
                    actions = topAppBarActions,
                    scrollBehavior = scrollBehavior
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                StandardBottomBar(navController = navController)
            }
        },
        snackbarHost = snackbarHost
    ) { paddingValues ->
        content(paddingValues)
    }
}

/**
 * StandardLayout için önizleme fonksiyonu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun StandardLayoutPreview() {
    val navController = rememberNavController()
    StandardLayout(
        navController = navController,
        title = "Başlık",
        showTopBar = true,
        showBackButton = true,
        showBottomBar = true
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
        }
    }
}
