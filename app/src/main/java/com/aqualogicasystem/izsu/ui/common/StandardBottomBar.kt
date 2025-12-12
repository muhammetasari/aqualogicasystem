package com.aqualogicasystem.izsu.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import com.aqualogicasystem.izsu.R

@Composable
fun StandardBottomBar(navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        stringResource(id = R.string.bottom_bar_home),
        stringResource(id = R.string.bottom_bar_search),
        stringResource(id = R.string.bottom_bar_appointment),
        stringResource(id = R.string.bottom_bar_profile),
        stringResource(id = R.string.bottom_bar_settings)
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    when (index) {
                        0 -> Icon(Icons.Default.Home, contentDescription = item)
                        1 -> Icon(Icons.Default.Preview, contentDescription = item)
                        2 -> Icon(Icons.Default.DateRange, contentDescription = item)
                        3 -> Icon(Icons.Default.AccountCircle, contentDescription = item)
                        4 -> Icon(Icons.Default.Settings, contentDescription = item)
                    }
                },
                label = {Text(item)},
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    when(index) {
                        0 -> navController.navigate("home")
                        1 -> navController.navigate("theme_demo")
                        //2 -> navController.navigate("")
                        3 -> navController.navigate("profile")
                        4 -> navController.navigate("settings")
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
