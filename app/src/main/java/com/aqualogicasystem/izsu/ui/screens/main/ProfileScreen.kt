package com.aqualogicasystem.izsu.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.aqualogicasystem.izsu.R
import com.aqualogicasystem.izsu.data.repository.fake.FakeAuthRepository
import com.aqualogicasystem.izsu.ui.common.StandardLayout
import com.aqualogicasystem.izsu.ui.components.ChangePasswordDialog
import com.aqualogicasystem.izsu.ui.components.ListMenuItem
import com.aqualogicasystem.izsu.ui.components.ProfileDetailDialog
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme
import com.aqualogicasystem.izsu.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    var showPasswordChangeDialog by remember { mutableStateOf(false) }
    var showProfileDetailDialog by remember { mutableStateOf(false) }
    val passwordChangeState by authViewModel.passwordChangeState.collectAsState()
    val user by authViewModel.currentUser.collectAsState()
    val context = LocalContext.current

    if (showPasswordChangeDialog) {
        ChangePasswordDialog(
            onDismiss = {
                showPasswordChangeDialog = false
                authViewModel.resetPasswordChangeState()
            },
            onChangePassword = { oldPassword, newPassword, confirmPassword ->
                authViewModel.changePassword(oldPassword, newPassword, confirmPassword, context)
            },
            state = passwordChangeState
        )
    }

    if (showProfileDetailDialog) {
        ProfileDetailDialog(
            user = user,
            onDismiss = { showProfileDetailDialog = false }
        )
    }

    LaunchedEffect(passwordChangeState.isSuccess) {
        if (passwordChangeState.isSuccess) {
            showPasswordChangeDialog = false
            authViewModel.resetPasswordChangeState()
            // Optionally, show a success snackbar
        }
    }

    StandardLayout(
        navController = navController,
        title = stringResource(id = R.string.account),
        showTopBar = true,
        showBackButton = true,
        showBottomBar = false,
        onNavigateBack = onNavigateBack,
        topAppBarActions = {
            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(id = R.string.content_description_settings),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Menu Items
            ListMenuItem(
                icon = Icons.Default.Person,
                title = stringResource(id = R.string.profile_details),
                hasTrailingIcon = true,
                onClick = { showProfileDetailDialog = true }
            )

            ListMenuItem(
                icon = Icons.Default.Lock,
                title = stringResource(id = R.string.password_settings),
                hasTrailingIcon = true,
                onClick = { showPasswordChangeDialog = true }
            )

            ListMenuItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                title = stringResource(id = R.string.logout),
                hasTrailingIcon = false,
                onClick = onLogout
            )
        }
    }
}



@Preview(showBackground = true, name = "Normal User")
@Composable
fun ProfileScreenPreview() {
    IzsuAppTheme {
        ProfileScreen(
            navController = rememberNavController(),
            authViewModel = AuthViewModel(FakeAuthRepository()),
            onLogout = {},
            onNavigateBack = {},
            onNavigateToSettings = {}
        )
    }
}

