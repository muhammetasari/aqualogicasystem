package com.aqualogicasystem.izsu.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.aqualogicasystem.izsu.data.model.AppThemeConfig

/**
 * Açık tema için renk şeması.
 *
 * Material Design 3 renk sistemi rollerine göre tanımlanmıştır. Her rengin kullanım amacı
 * Material Design kurallarına göredir.
 */
private val LightColorScheme = lightColorScheme(
    /** Ana eylemler, belirgin düğmeler ve önemli bileşenler için kullanılır. */
    primary = primaryLight,
    /** `primary` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onPrimary = onPrimaryLight,
    /** `primary` renginden daha az dikkat çekmesi gereken bileşenler için kullanılır (örn. tonlu düğmeler). */
    primaryContainer = primaryContainerLight,
    /** `primaryContainer` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onPrimaryContainer = onPrimaryContainerLight,
    /** İkincil eylemler, filtreler ve daha az önemli bileşenler için kullanılır. */
    secondary = secondaryLight,
    /** `secondary` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onSecondary = onSecondaryLight,
    /** `secondary` renginden daha az dikkat çekmesi gereken bileşenler için kullanılır. */
    secondaryContainer = secondaryContainerLight,
    /** `secondaryContainer` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onSecondaryContainer = onSecondaryContainerLight,
    /** Üçüncül eylemler ve tamamlayıcı bileşenler için kullanılır. */
    tertiary = tertiaryLight,
    /** `tertiary` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onTertiary = onTertiaryLight,
    /** `tertiary` renginden daha az dikkat çekmesi gereken bileşenler için kullanılır. */
    tertiaryContainer = tertiaryContainerLight,
    /** `tertiaryContainer` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onTertiaryContainer = onTertiaryContainerLight,
    /** Hata durumlarını ve hata mesajlarını belirtmek için kullanılır. */
    error = errorLight,
    /** `error` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onError = onErrorLight,
    /** Hata durumlarını daha az dikkat çekici bir şekilde belirtmek için kullanılır. */
    errorContainer = errorContainerLight,
    /** `errorContainer` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onErrorContainer = onErrorContainerLight,
    /** Uygulamanın genel arka plan rengidir. */
    background = backgroundLight,
    /** `background` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onBackground = onBackgroundLight,
    /** Kartlar, diyaloglar ve menüler gibi bileşenlerin yüzey rengidir. */
    surface = surfaceLight,
    /** `surface` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onSurface = onSurfaceLight,
    /** `surface` renginin farklı bir tonu, genellikle daha az vurgulu yüzeyler için. */
    surfaceVariant = surfaceVariantLight,
    /** `surfaceVariant` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onSurfaceVariant = onSurfaceVariantLight,
    /** Sınırlar, ayırıcılar ve dekoratif çizgiler için kullanılır. */
    outline = outlineLight,
    /** `outline` renginin daha az dikkat çekici bir varyantı. */
    outlineVariant = outlineVariantLight,
    /** İçeriği geçici olarak karartmak için kullanılır (örn. diyalog arkası). */
    scrim = scrimLight,
    /** `surface` renginin tersi, genellikle snackbar gibi geçici bildirimlerde kullanılır. */
    inverseSurface = inverseSurfaceLight,
    /** `inverseSurface` rengi üzerindeki metin ve ikonlar için kullanılır. */
    inverseOnSurface = inverseOnSurfaceLight,
    /** `primary` renginin tersi, `inverseSurface` üzerinde ana eylemler için kullanılır. */
    inversePrimary = inversePrimaryLight,
    /** Yüzeyin en koyu tonu. */
    surfaceDim = surfaceDimLight,
    /** Yüzeyin en parlak tonu. */
    surfaceBright = surfaceBrightLight,
    /** En az vurgulu konteyner rengi. */
    surfaceContainerLowest = surfaceContainerLowestLight,
    /** Düşük vurgulu konteyner rengi. */
    surfaceContainerLow = surfaceContainerLowLight,
    /** Varsayılan konteyner rengi. */
    surfaceContainer = surfaceContainerLight,
    /** Yüksek vurgulu konteyner rengi. */
    surfaceContainerHigh = surfaceContainerHighLight,
    /** En yüksek vurgulu konteyner rengi. */
    surfaceContainerHighest = surfaceContainerHighestLight,
)

/**
 * Koyu tema için renk şeması.
 *
 * Material Design 3 renk sistemi rollerine göre tanımlanmıştır. Her rengin kullanım amacı
 * Material Design kurallarına göredir.
 */
private val DarkColorScheme = darkColorScheme(
    /** Ana eylemler, belirgin düğmeler ve önemli bileşenler için kullanılır. */
    primary = primaryDark,
    /** `primary` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onPrimary = onPrimaryDark,
    /** `primary` renginden daha az dikkat çekmesi gereken bileşenler için kullanılır (örn. tonlu düğmeler). */
    primaryContainer = primaryContainerDark,
    /** `primaryContainer` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onPrimaryContainer = onPrimaryContainerDark,
    /** İkincil eylemler, filtreler ve daha az önemli bileşenler için kullanılır. */
    secondary = secondaryDark,
    /** `secondary` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onSecondary = onSecondaryDark,
    /** `secondary` renginden daha az dikkat çekmesi gereken bileşenler için kullanılır. */
    secondaryContainer = secondaryContainerDark,
    /** `secondaryContainer` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onSecondaryContainer = onSecondaryContainerDark,
    /** Üçüncül eylemler ve tamamlayıcı bileşenler için kullanılır. */
    tertiary = tertiaryDark,
    /** `tertiary` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onTertiary = onTertiaryDark,
    /** `tertiary` renginden daha az dikkat çekmesi gereken bileşenler için kullanılır. */
    tertiaryContainer = tertiaryContainerDark,
    /** `tertiaryContainer` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onTertiaryContainer = onTertiaryContainerDark,
    /** Hata durumlarını ve hata mesajlarını belirtmek için kullanılır. */
    error = errorDark,
    /** `error` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onError = onErrorDark,
    /** Hata durumlarını daha az dikkat çekici bir şekilde belirtmek için kullanılır. */
    errorContainer = errorContainerDark,
    /** `errorContainer` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onErrorContainer = onErrorContainerDark,
    /** Uygulamanın genel arka plan rengidir. */
    background = backgroundDark,
    /** `background` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onBackground = onBackgroundDark,
    /** Kartlar, diyaloglar ve menüler gibi bileşenlerin yüzey rengidir. */
    surface = surfaceDark,
    /** `surface` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onSurface = onSurfaceDark,
    /** `surface` renginin farklı bir tonu, genellikle daha az vurgulu yüzeyler için. */
    surfaceVariant = surfaceVariantDark,
    /** `surfaceVariant` rengi üzerindeki metin ve ikonlar için kullanılır. */
    onSurfaceVariant = onSurfaceVariantDark,
    /** Sınırlar, ayırıcılar ve dekoratif çizgiler için kullanılır. */
    outline = outlineDark,
    /** `outline` renginin daha az dikkat çekici bir varyantı. */
    outlineVariant = outlineVariantDark,
    /** İçeriği geçici olarak karartmak için kullanılır (örn. diyalog arkası). */
    scrim = scrimDark,
    /** `surface` renginin tersi, genellikle snackbar gibi geçici bildirimlerde kullanılır. */
    inverseSurface = inverseSurfaceDark,
    /** `inverseSurface` rengi üzerindeki metin ve ikonlar için kullanılır. */
    inverseOnSurface = inverseOnSurfaceDark,
    /** `primary` renginin tersi, `inverseSurface` üzerinde ana eylemler için kullanılır. */
    inversePrimary = inversePrimaryDark,
    /** Yüzeyin en koyu tonu. */
    surfaceDim = surfaceDimDark,
    /** Yüzeyin en parlak tonu. */
    surfaceBright = surfaceBrightDark,
    /** En az vurgulu konteyner rengi. */
    surfaceContainerLowest = surfaceContainerLowestDark,
    /** Düşük vurgulu konteyner rengi. */
    surfaceContainerLow = surfaceContainerLowDark,
    /** Varsayılan konteyner rengi. */
    surfaceContainer = surfaceContainerDark,
    /** Yüksek vurgulu konteyner rengi. */
    surfaceContainerHigh = surfaceContainerHighDark,
    /** En yüksek vurgulu konteyner rengi. */
    surfaceContainerHighest = surfaceContainerHighestDark,
)

/**
 * İzsu App Theme - Material Design 3 standartlarına uygun özel tema sistemi
 *
 * Dinamik tema desteği kaldırılmıştır. Uygulama, tüm cihazlarda tutarlı bir görünüm için
 * özel olarak tanımlanmış renk paletini kullanır.
 *
 * @param themeConfig The theme configuration to apply (FOLLOW_SYSTEM, LIGHT, or DARK)
 * @param content Temalı içerik composable'ı.
 */
@Composable
fun IzsuAppTheme(
    themeConfig: AppThemeConfig = AppThemeConfig.FOLLOW_SYSTEM,
    content: @Composable () -> Unit
) {
    val systemInDarkTheme = isSystemInDarkTheme()

    val darkTheme = when (themeConfig) {
        AppThemeConfig.FOLLOW_SYSTEM -> systemInDarkTheme
        AppThemeConfig.LIGHT -> false
        AppThemeConfig.DARK -> true
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
