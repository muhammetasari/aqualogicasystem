package com.aqualogicasystem.izsu.ui.theme.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aqualogicasystem.izsu.ui.components.ListMenuItem
import com.aqualogicasystem.izsu.ui.components.ListMenuItemStyle
import com.aqualogicasystem.izsu.ui.components.IzsuLogo
import com.aqualogicasystem.izsu.ui.components.TermsOfUseDialog
import com.aqualogicasystem.izsu.ui.components.PrivacyPolicyDialog
import com.aqualogicasystem.izsu.ui.components.TermsPrivacyText
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme
import com.aqualogicasystem.izsu.ui.common.StandartTopAppBar
import kotlinx.coroutines.launch

/**
 * Theme Demo Screen
 *
 * Bu ekran, İzsu uygulamasının tema sistemini gösterir.
 * Tüm renk ve tipografi stillerinin nasıl göründüğünü test etmek için kullanılır.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeDemoScreen(
    onNavigateBack: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            StandartTopAppBar(
                title = "Tema Demosu",
                showBackButton = true,
                onNavigateBack = onNavigateBack,
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Typography Section
            TypographySection()

            HorizontalDivider()

            // Colors Section
            ColorsSection()

            HorizontalDivider()

            // Components Section
            ComponentsSection(snackbarHostState = snackbarHostState)
        }
    }
}

@Composable
private fun TypographySection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "📝 Tipografi Örnekleri",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        // Display Styles
        Text(
            text = "Display Large",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Display Medium",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Display Small",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Headline Styles
        Text(
            text = "Headline Large",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Headline Medium",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Headline Small",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Title Styles
        Text(
            text = "Title Large",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Title Medium",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Title Small",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Body Styles
        Text(
            text = "Body Large - Bu metin büyük gövde stili kullanır",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Body Medium - Bu metin orta gövde stili kullanır",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Body Small - Bu metin küçük gövde stili kullanır",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Label Styles
        Text(
            text = "Label Large",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Label Medium",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Label Small",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ColorsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "🎨 Renk Paleti",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        // Primary Colors
        Text(
            text = "Primary Colors",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )
        ColorItem("Primary", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
        ColorItem("On Primary", MaterialTheme.colorScheme.onPrimary, MaterialTheme.colorScheme.primary)
        ColorItem("Primary Container", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
        ColorItem("On Primary Container", MaterialTheme.colorScheme.onPrimaryContainer, MaterialTheme.colorScheme.primaryContainer)

        // Secondary Colors
        Text(
            text = "Secondary Colors",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )
        ColorItem("Secondary", MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary)
        ColorItem("On Secondary", MaterialTheme.colorScheme.onSecondary, MaterialTheme.colorScheme.secondary)
        ColorItem("Secondary Container", MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
        ColorItem("On Secondary Container", MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.colorScheme.secondaryContainer)

        // Tertiary Colors
        Text(
            text = "Tertiary Colors",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )
        ColorItem("Tertiary", MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary)
        ColorItem("On Tertiary", MaterialTheme.colorScheme.onTertiary, MaterialTheme.colorScheme.tertiary)

        // Surface Colors
        Text(
            text = "Surface Colors",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )
        ColorItem("Background", MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.onBackground)
        ColorItem("On Background", MaterialTheme.colorScheme.onBackground, MaterialTheme.colorScheme.background)
        ColorItem("Surface", MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface)
        ColorItem("On Surface", MaterialTheme.colorScheme.onSurface, MaterialTheme.colorScheme.surface)
        ColorItem("Surface Variant", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
        ColorItem("On Surface Variant", MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.surfaceVariant)

        // Error Colors
        Text(
            text = "Error Colors",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )
        ColorItem("Error", MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.onError)
        ColorItem("On Error", MaterialTheme.colorScheme.onError, MaterialTheme.colorScheme.error)
        ColorItem("Error Container", MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
        ColorItem("On Error Container", MaterialTheme.colorScheme.onErrorContainer, MaterialTheme.colorScheme.errorContainer)
    }
}

@Composable
private fun ColorItem(
    name: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = backgroundColor,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge,
                color = textColor
            )
            Text(
                text = "#${backgroundColor.value.toString(16).uppercase().substring(2, 8)}",
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.8f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComponentsSection(snackbarHostState: SnackbarHostState) {
    val coroutineScope = rememberCoroutineScope()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "🧩 Bileşen Örnekleri",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        // Material3 Buttons
        Text(
            text = "Butonlar",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Primary Button")
        }

        OutlinedButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Outlined Button")
        }

        TextButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Text Button")
        }

        FilledTonalButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Filled Tonal Button")
        }

        // Text Fields Section
        Text(
            text = "Metin Alanları",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        var textValue by remember { mutableStateOf("") }
        OutlinedTextField(
            value = textValue,
            onValueChange = { textValue = it },
            placeholder = {
                Text(
                    text = "Email veya Kullanıcı Adı",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )

        var passwordValue by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = passwordValue,
            onValueChange = { passwordValue = it },
            placeholder = {
                Text(
                    text = "Şifre",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Şifre görünürlüğü"
                    )
                }
            }
        )

        Column {
            OutlinedTextField(
                value = "invalid@email",
                onValueChange = { },
                placeholder = {
                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = true,
                singleLine = true,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                textStyle = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Geçersiz email formatı",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        // Custom Info Cards Section
        Text(
            text = "Custom Info Cards",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Kullanıcı Adı
                Column {
                    Text(
                        text = "Kullanıcı Adı",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "john_doe",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider()
                // Email
                Column {
                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "user@example.com",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider()
                // Durum
                Column {
                    Text(
                        text = "Durum",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Aktif",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // IzsuLogo Section
        Text(
            text = "Logo",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                IzsuLogo(size = 80.dp)
            }
        }

        // FAB Section
        Text(
            text = "Floating Action Buttons",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FloatingActionButton(onClick = { }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }

            SmallFloatingActionButton(onClick = { }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }

            ExtendedFloatingActionButton(
                onClick = { },
                icon = { Icon(Icons.Default.Navigation, contentDescription = "Navigate") },
                text = { Text("Yönlendir") }
            )
        }

        // Icon Buttons Section
        Text(
            text = "Icon Buttons",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Home, contentDescription = "Home")
            }

            FilledIconButton(onClick = { }) {
                Icon(Icons.Default.Favorite, contentDescription = "Favorite")
            }

            FilledTonalIconButton(onClick = { }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }

            OutlinedIconButton(onClick = { }) {
                Icon(Icons.Default.Person, contentDescription = "Profile")
            }
        }

        // Chips Section
        Text(
            text = "Chips",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AssistChip(
                onClick = { },
                label = { Text("Assist") },
                leadingIcon = { Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )

            FilterChip(
                selected = true,
                onClick = { },
                label = { Text("Filter") },
                leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )

            SuggestionChip(
                onClick = { },
                label = { Text("Suggestion") }
            )
        }

        // Switches and Checkboxes
        Text(
            text = "Switches & Checkboxes",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Switch", style = MaterialTheme.typography.bodyLarge)
                    var switchState by remember { mutableStateOf(true) }
                    Switch(
                        checked = switchState,
                        onCheckedChange = { switchState = it }
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Checkbox", style = MaterialTheme.typography.bodyLarge)
                    var checkboxState by remember { mutableStateOf(true) }
                    Checkbox(
                        checked = checkboxState,
                        onCheckedChange = { checkboxState = it }
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Radio Button", style = MaterialTheme.typography.bodyLarge)
                    RadioButton(
                        selected = true,
                        onClick = { }
                    )
                }
            }
        }

        // Sliders Section
        Text(
            text = "Sliders",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var sliderValue by remember { mutableStateOf(0.5f) }
                Text("Slider: ${(sliderValue * 100).toInt()}%")
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it }
                )
            }
        }

        // Cards Section
        Text(
            text = "Cards",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Card Başlığı",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Bu bir card içeriği örneğidir. Card'lar içerik gruplamak için kullanılır.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Elevated Card",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Bu yükseltilmiş bir card örneğidir.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Outlined Card",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Bu çerçeveli bir card örneğidir.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Badges Section
        Text(
            text = "Badges",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BadgedBox(
                badge = { Badge { Text("3") } }
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
            }

            BadgedBox(
                badge = { Badge() }
            ) {
                Icon(Icons.Default.Email, contentDescription = "Email")
            }
        }

        // Progress Indicators
        Text(
            text = "Progress Indicators",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator()
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                LinearProgressIndicator(
                    progress = { 0.65f },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Dialogs Section
        Text(
            text = "Dialogs",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        var showTermsDialog by remember { mutableStateOf(false) }
        var showPrivacyDialog by remember { mutableStateOf(false) }
        var showAlertDialog by remember { mutableStateOf(false) }

        if (showTermsDialog) {
            TermsOfUseDialog(onDismiss = { showTermsDialog = false })
        }

        if (showPrivacyDialog) {
            PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })
        }

        if (showAlertDialog) {
            AlertDialog(
                onDismissRequest = { showAlertDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info"
                    )
                },
                title = { Text("Bilgi") },
                text = { Text("Bu bir standart Material3 AlertDialog örneğidir.") },
                confirmButton = {
                    TextButton(onClick = { showAlertDialog = false }) {
                        Text("Tamam")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAlertDialog = false }) {
                        Text("İptal")
                    }
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showTermsDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("Kullanım Koşulları")
            }

            Button(
                onClick = { showPrivacyDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Text("Gizlilik")
            }
        }

        Button(
            onClick = { showAlertDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Alert Dialog Göster")
        }

        // Terms & Privacy Text
        Text(
            text = "Terms & Privacy Clickable Text",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.padding(16.dp)) {
                TermsPrivacyText(
                    onTermsClick = { showTermsDialog = true },
                    onPrivacyClick = { showPrivacyDialog = true }
                )
            }
        }

        // Navigation Bar Example
        Text(
            text = "Navigation Bar",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            var selectedNavItem by remember { mutableIntStateOf(0) }
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                NavigationBarItem(
                    selected = selectedNavItem == 0,
                    onClick = { selectedNavItem = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Ana Sayfa") }
                )
                NavigationBarItem(
                    selected = selectedNavItem == 1,
                    onClick = { selectedNavItem = 1 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                    label = { Text("Favoriler") }
                )
                NavigationBarItem(
                    selected = selectedNavItem == 2,
                    onClick = { selectedNavItem = 2 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profil") }
                )
            }
        }

        // Top App Bar Example
        Text(
            text = "Top App Bar",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            TopAppBar(
                title = { Text("Örnek Başlık") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Search, contentDescription = "Ara")
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Daha Fazla")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        // Snackbars Section
        Text(
            text = "Snackbars",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Bu bir basit Snackbar mesajıdır",
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Basit", style = MaterialTheme.typography.labelMedium)
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Öğe silindi",
                            actionLabel = "Geri Al",
                            duration = SnackbarDuration.Long
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            snackbarHostState.showSnackbar("İşlem geri alındı")
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Aksiyonlu", style = MaterialTheme.typography.labelMedium)
            }
        }

        // Dropdown Menu Section
        Text(
            text = "Dropdown Menu",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        var expandedMenu by remember { mutableStateOf(false) }
        var selectedOption by remember { mutableStateOf("Seçenek 1") }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box {
                    Button(
                        onClick = { expandedMenu = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Menü Aç: $selectedOption")
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Seçenek 1") },
                            onClick = {
                                selectedOption = "Seçenek 1"
                                expandedMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Seçenek 2") },
                            onClick = {
                                selectedOption = "Seçenek 2"
                                expandedMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null) }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Seçenek 3") },
                            onClick = {
                                selectedOption = "Seçenek 3"
                                expandedMenu = false
                            },
                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
                        )
                    }
                }

                // Exposed Dropdown Menu
                var expandedExposed by remember { mutableStateOf(false) }
                var selectedExposedOption by remember { mutableStateOf("Seçim yapın") }
                val options = listOf("Türkiye", "Amerika", "Almanya", "Fransa", "İngiltere")

                ExposedDropdownMenuBox(
                    expanded = expandedExposed,
                    onExpandedChange = { expandedExposed = it }
                ) {
                    OutlinedTextField(
                        value = selectedExposedOption,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ülke Seçin") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedExposed) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        colors = OutlinedTextFieldDefaults.colors()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedExposed,
                        onDismissRequest = { expandedExposed = false }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedExposedOption = option
                                    expandedExposed = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Bottom Sheet Section
        Text(
            text = "Bottom Sheet",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        var showBottomSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()

        Button(
            onClick = { showBottomSheet = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Bottom Sheet Göster")
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Bottom Sheet Başlığı",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Bu bir Modal Bottom Sheet örneğidir. Kullanıcıya ek bilgi veya seçenekler sunmak için kullanılır.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("Liste Öğesi 1") },
                        leadingContent = { Icon(Icons.Default.Share, contentDescription = null) },
                        modifier = Modifier.clickable { }
                    )
                    ListItem(
                        headlineContent = { Text("Liste Öğesi 2") },
                        leadingContent = { Icon(Icons.Default.Link, contentDescription = null) },
                        modifier = Modifier.clickable { }
                    )
                    ListItem(
                        headlineContent = { Text("Liste Öğesi 3") },
                        leadingContent = { Icon(Icons.Default.Download, contentDescription = null) },
                        modifier = Modifier.clickable { }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // List Items Section
        Text(
            text = "List Items",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column {
                ListItem(
                    headlineContent = { Text("Tek Satır Liste Öğesi") },
                    leadingContent = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("İki Satır Liste Öğesi") },
                    supportingContent = { Text("Destek metni burada görünür") },
                    leadingContent = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    trailingContent = {
                        Text("12:45")
                    }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("Üç Satır Liste Öğesi") },
                    supportingContent = {
                        Text("Bu daha uzun bir destek metnidir. Liste öğeleri için detaylı açıklama sağlar.")
                    },
                    leadingContent = {
                        Icon(Icons.Default.Image, contentDescription = null)
                    },
                    trailingContent = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Daha fazla")
                        }
                    }
                )
            }
        }

        // Text Field Variants Section
        Text(
            text = "Text Field Variants",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        var filledTextValue by remember { mutableStateOf("") }
        TextField(
            value = filledTextValue,
            onValueChange = { filledTextValue = it },
            placeholder = { Text("Filled TextField") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Date Picker Section
        Text(
            text = "Date Picker",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        var showDatePicker by remember { mutableStateOf(false) }
        var selectedDate by remember { mutableStateOf<Long?>(null) }

        Button(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.DateRange, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                text = selectedDate?.let {
                    val date = java.util.Date(it)
                    java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale("tr")).format(date)
                } ?: "Tarih Seç"
            )
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedDate = datePickerState.selectedDateMillis
                            showDatePicker = false
                        }
                    ) {
                        Text("Tamam")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("İptal")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Range Slider Section
        Text(
            text = "Range Slider",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var rangeSliderValue by remember { mutableStateOf(0.2f..0.8f) }
                Text("Aralık: ${(rangeSliderValue.start * 100).toInt()}% - ${(rangeSliderValue.endInclusive * 100).toInt()}%")
                RangeSlider(
                    value = rangeSliderValue,
                    onValueChange = { rangeSliderValue = it },
                    valueRange = 0f..1f
                )
            }
        }

        // Segmented Button Section
        Text(
            text = "Segmented Buttons",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Single Choice
                Text("Tek Seçim:", style = MaterialTheme.typography.bodyMedium)
                var selectedIndex by remember { mutableIntStateOf(0) }
                val options = listOf("Gün", "Hafta", "Ay")

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEachIndexed { index, label ->
                        SegmentedButton(
                            selected = selectedIndex == index,
                            onClick = { selectedIndex = index },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size
                            )
                        ) {
                            Text(label)
                        }
                    }
                }

                // Multi Choice
                Text("Çoklu Seçim:", style = MaterialTheme.typography.bodyMedium)
                var checkedList by remember { mutableStateOf(listOf(0)) }
                val multiOptions = listOf("Kırmızı", "Yeşil", "Mavi")

                MultiChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    multiOptions.forEachIndexed { index, label ->
                        SegmentedButton(
                            checked = index in checkedList,
                            onCheckedChange = {
                                checkedList = if (index in checkedList) {
                                    checkedList - index
                                } else {
                                    checkedList + index
                                }
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = multiOptions.size
                            )
                        ) {
                            Text(label)
                        }
                    }
                }
            }
        }

        // Navigation Drawer Section
        Text(
            text = "Navigation Drawer",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        val drawerState = rememberDrawerState(DrawerValue.Closed)
        var selectedDrawerItem by remember { mutableIntStateOf(0) }

        Card(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                                Text(
                                    "Menü",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp)
                                )
                                HorizontalDivider()
                                Spacer(Modifier.height(12.dp))
                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                    label = { Text("Ana Sayfa") },
                                    selected = selectedDrawerItem == 0,
                                    onClick = {
                                        selectedDrawerItem = 0
                                        coroutineScope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                                    label = { Text("Favoriler") },
                                    selected = selectedDrawerItem == 1,
                                    onClick = {
                                        selectedDrawerItem = 1
                                        coroutineScope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                                NavigationDrawerItem(
                                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                    label = { Text("Ayarlar") },
                                    selected = selectedDrawerItem == 2,
                                    onClick = {
                                        selectedDrawerItem = 2
                                        coroutineScope.launch { drawerState.close() }
                                    },
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        }
                    },
                    content = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Button(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                    Text("Drawer Aç")
                                }
                            }
                        }
                    }
                )
            }
        }

        // Search Bar Section
        Text(
            text = "Search Bar",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        var searchQuery by remember { mutableStateOf("") }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ara...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Temizle")
                            }
                        }
                    },
                    singleLine = true,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                )
                if (searchQuery.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Arama sonuçları: \"$searchQuery\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Tooltip Section
        Text(
            text = "Tooltips",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Plain Tooltip
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip {
                            Text("Bu bir Plain Tooltip")
                        }
                    },
                    state = rememberTooltipState()
                ) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Info, contentDescription = "Info")
                    }
                }

                // Rich Tooltip
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                    tooltip = {
                        RichTooltip(
                            title = { Text("Zengin Tooltip") },
                            text = { Text("Bu daha detaylı bir tooltip örneğidir") }
                        )
                    },
                    state = rememberTooltipState()
                ) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            }
        }

        // Top App Bar Variants Section
        Text(
            text = "Top App Bar Variants",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Center Aligned Top App Bar
        Card(modifier = Modifier.fillMaxWidth()) {
            CenterAlignedTopAppBar(
                title = { Text("Ortalanmış Başlık") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Daha fazla")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Medium Top App Bar
        Card(modifier = Modifier.fillMaxWidth()) {
            MediumTopAppBar(
                title = { Text("Orta Boyut Başlık") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Large Top App Bar
        Card(modifier = Modifier.fillMaxWidth()) {
            LargeTopAppBar(
                title = { Text("Büyük Boyut Başlık") },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            )
        }

        // List Menu Items Section
        ListMenuItemsSection()
    }
}

@Composable
private fun ListMenuItemsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "📋 List Menu Items",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Basit Kullanım",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        // 1. Basit menu item
        ListMenuItem(
            icon = Icons.Default.Person,
            title = "Profil",
            onClick = { /* Tıklama işlemi */ },
            hasTrailingIcon = true
        )

        // 2. Alt başlıklı menu item
        ListMenuItem(
            icon = Icons.Default.Settings,
            title = "Ayarlar",
            subtitle = "Uygulama tercihlerini yönet",
            onClick = { /* Tıklama işlemi */ },
            hasTrailingIcon = true
        )

        Text(
            text = "Switch ile Kullanım",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        // 3. Switch button ile
        var isDarkMode by remember { mutableStateOf(false) }
        ListMenuItem(
            icon = Icons.Default.DarkMode,
            title = "Karanlık Mod",
            subtitle = "Gece temasını etkinleştir",
            onClick = { /* İsteğe bağlı */ },
            showSwitchesButton = true,
            switchState = isDarkMode,
            onSwitchChange = { isDarkMode = it }
        )

        Text(
            text = "Badge ve Özelleştirilmiş Stiller",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        // 4. Badge ile bildirim sayısı
        ListMenuItem(
            icon = Icons.Default.Notifications,
            title = "Bildirimler",
            subtitle = "Yeni mesajlar",
            onClick = { /* Tıklama işlemi */ },
            badge = "5",
            hasTrailingIcon = true
        )

        // 5. Özelleştirilmiş renkler
        ListMenuItem(
            icon = Icons.Default.Favorite,
            title = "Favoriler",
            subtitle = "Beğendiğin içerikler",
            onClick = { /* Tıklama işlemi */ },
            hasTrailingIcon = true,
            style = ListMenuItemStyle(
                iconBackgroundColor = Color(0xFFFFEBEE),
                iconTintColor = Color(0xFFE91E63),
                titleFontSize = 18.sp
            )
        )

        Text(
            text = "Seçili Durum ve Animasyonlar",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        // 6. Seçili durum gösterimi
        var isSelected by remember { mutableStateOf(false) }
        ListMenuItem(
            icon = Icons.Default.CheckCircle,
            title = "Seçilebilir Item",
            subtitle = if (isSelected) "Seçildi" else "Seçmek için tıkla",
            onClick = { isSelected = !isSelected },
            selected = isSelected,
            style = ListMenuItemStyle(
                backgroundColor = MaterialTheme.colorScheme.surface,
                selectedBackgroundColor = MaterialTheme.colorScheme.primaryContainer,
                cardElevation = 2.dp
            )
        )

        Text(
            text = "Devre Dışı ve Long Press",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        // 7. Devre dışı bırakılmış
        ListMenuItem(
            icon = Icons.Default.Block,
            title = "Devre Dışı Item",
            subtitle = "Bu öğe tıklanamaz",
            onClick = { /* Çalışmaz */ },
            enabled = false,
            hasTrailingIcon = true
        )

        // 8. Long press desteği
        var longPressCount by remember { mutableStateOf(0) }
        ListMenuItem(
            icon = Icons.Default.TouchApp,
            title = "Uzun Bas",
            subtitle = "Uzun basma sayısı: $longPressCount",
            onClick = { /* Normal tıklama */ },
            onLongClick = { longPressCount++ }
        )

        Text(
            text = "Özel İçerik",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        // 9. Özel trailing content
        ListMenuItem(
            icon = Icons.Default.Star,
            title = "Değerlendirme",
            subtitle = "4.5 / 5.0",
            onClick = { /* Tıklama işlemi */ },
            trailingContent = {
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < 4) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        )

        // 10. Özel leading content
        ListMenuItem(
            icon = Icons.Default.Person, // Kullanılmayacak
            title = "Özel Avatar",
            subtitle = "Custom leading content",
            onClick = { /* Tıklama işlemi */ },
            hasTrailingIcon = true,
            leadingContent = {
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "AB",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        )

        Text(
            text = "Gelişmiş Özelleştirme",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 8.dp)
        )

        // 11. Tam özelleştirilmiş stil
        ListMenuItem(
            icon = Icons.Default.Palette,
            title = "Özel Tasarım",
            subtitle = "Tüm parametreler özelleştirildi",
            onClick = { /* Tıklama işlemi */ },
            hasTrailingIcon = true,
            style = ListMenuItemStyle(
                iconSize = 56.dp,
                iconContentSize = 28.dp,
                iconBackgroundColor = Color(0xFFE3F2FD),
                iconTintColor = Color(0xFF1976D2),
                titleFontSize = 20.sp,
                subtitleFontSize = 15.sp,
                horizontalPadding = 20.dp,
                verticalPadding = 20.dp,
                verticalSpacing = 6.dp,
                cardElevation = 4.dp,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        // 12. Bilgi kartı görünümü
        var expanded by remember { mutableStateOf(false) }
        ListMenuItem(
            icon = Icons.Default.Info,
            title = "Detaylı Bilgi",
            subtitle = if (expanded) "Tıklayarak daralt" else "Tıklayarak genişlet",
            onClick = { expanded = !expanded },
            badge = if (expanded) "AÇIK" else null,
            style = ListMenuItemStyle(
                cardElevation = if (expanded) 8.dp else 2.dp,
                backgroundColor = if (expanded)
                    MaterialTheme.colorScheme.secondaryContainer
                else
                    MaterialTheme.colorScheme.surface,
                cardShape = MaterialTheme.shapes.large
            )
        )
    }
}

// Preview for Light Theme
@Preview(name = "Light Theme", showBackground = true)
@Composable
private fun ThemeDemoLightPreview() {
    IzsuAppTheme( ) {
        ThemeDemoScreen()
    }
}