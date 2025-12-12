package com.aqualogicasystem.izsu.ui.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview

/**
 * Özelleştirilebilir standart üst uygulama çubuğu.
 *
 * @param title Başlık metni
 * @param modifier Modifier
 * @param showBackButton Geri butonunun gösterilip gösterilmeyeceği
 * @param onNavigateBack Geri butonuna tıklandığında çalışacak fonksiyon
 * @param navigationIcon Özel navigasyon ikonu (showBackButton true ise bu parametre göz ardı edilir)
 * @param actions Sağ tarafta gösterilecek eylem butonları
 * @param titleTextStyle Başlık metninin stili (varsayılan: 20.sp, SemiBold)
 * @param backgroundColor Arka plan rengi (varsayılan: MaterialTheme.colorScheme.surface)
 * @param contentColor İçerik rengi (varsayılan: MaterialTheme.colorScheme.onSurface)
 * @param scrollBehavior Kaydırma davranışı (varsayılan: TopAppBarDefaults.exitUntilCollapsedScrollBehavior())
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandartTopAppBar(
    modifier: Modifier = Modifier,
    title: String = "",
    showBackButton: Boolean = true,
    onNavigateBack: () -> Unit = {},
    navigationIcon: ImageVector? = null,
    actions: @Composable RowScope.() -> Unit = {},
    titleTextStyle: TextStyle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    ),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    scrollBehavior: TopAppBarScrollBehavior? = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = titleTextStyle,
                color = contentColor
            )
        },
        navigationIcon = {
            when {
                showBackButton -> {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = contentColor
                        )
                    }
                }
                navigationIcon != null -> {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = navigationIcon,
                            contentDescription = "Navigation",
                            tint = contentColor
                        )
                    }
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = backgroundColor,
            navigationIconContentColor = contentColor,
            titleContentColor = contentColor,
            actionIconContentColor = contentColor
        ),
        scrollBehavior = scrollBehavior
    )
}

/**
 * StandartTopAppBar için önizleme fonksiyonu - Temel kullanım.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Temel Kullanım")
@Composable
fun StandartTopAppBarPreview() {
    StandartTopAppBar(
        title = "Başlık",
        showBackButton = true,
        onNavigateBack = {}
    )
}

/**
 * StandartTopAppBar için önizleme fonksiyonu - Geri butonu olmadan.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Geri Butonu Yok")
@Composable
fun StandartTopAppBarNoBackButtonPreview() {
    StandartTopAppBar(
        title = "Ana Sayfa",
        showBackButton = false
    )
}

/**
 * StandartTopAppBar için önizleme fonksiyonu - Eylem butonları ile.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Eylem Butonları")
@Composable
fun StandartTopAppBarWithActionsPreview() {
    StandartTopAppBar(
        title = "Ayarlar",
        showBackButton = true,
        onNavigateBack = {},
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.StarHalf, // Örnek için
                    contentDescription = "Paylaş"
                )
            }
        }
    )
}
