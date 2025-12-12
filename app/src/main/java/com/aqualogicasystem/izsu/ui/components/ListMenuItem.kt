package com.aqualogicasystem.izsu.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aqualogicasystem.izsu.R

/**
 * Style configuration for ListMenuItem component
 */
data class ListMenuItemStyle(
    val iconSize: Dp = 40.dp,
    val iconContentSize: Dp = 20.dp,
    val iconBackgroundColor: Color? = null,
    val iconTintColor: Color? = null,
    val titleTextStyle: TextStyle? = null,
    val titleFontSize: TextUnit = 16.sp,
    val subtitleTextStyle: TextStyle? = null,
    val subtitleFontSize: TextUnit = 14.sp,
    val horizontalPadding: Dp = 16.dp,
    val verticalPadding: Dp = 16.dp,
    val verticalSpacing: Dp = 4.dp,
    val cardElevation: Dp = 0.dp,
    val cardShape: Shape? = null,
    val backgroundColor: Color? = null,
    val selectedBackgroundColor: Color? = null
)

/**
 * Reusable list menu item component with icon, title, optional subtitle, and various trailing elements.
 *
 * @param icon The icon to display on the left side
 * @param title The main title text
 * @param onClick Action to perform when the item is clicked
 * @param modifier Optional modifier for the component
 * @param subtitle Optional subtitle text below the title
 * @param hasTrailingIcon Whether to show a chevron icon on the right (for navigation)
 * @param trailingContent Custom composable content to display on the right
 * @param showSwitchesButton Whether to show a switch toggle on the right
 * @param switchState The current state of the switch (if showSwitchesButton is true)
 * @param onSwitchChange Callback when switch state changes
 * @param badge Optional badge text or count to display (e.g., notification count)
 * @param enabled Whether the item is enabled and clickable
 * @param selected Whether the item is in selected state
 * @param onLongClick Optional long press action
 * @param onDismiss Optional dismiss action
 * @param style Custom style configuration
 * @param leadingContent Optional custom leading content (overrides default icon)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    hasTrailingIcon: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null,
    showSwitchesButton: Boolean = false,
    switchState: Boolean = false,
    onSwitchChange: ((Boolean) -> Unit)? = null,
    badge: String? = null,
    enabled: Boolean = true,
    selected: Boolean = false,
    onLongClick: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    style: ListMenuItemStyle = ListMenuItemStyle(),
    leadingContent: (@Composable () -> Unit)? = null
) {
    // Animated colors for selection state
    val animatedBackgroundColor by animateColorAsState(
        targetValue = when {
            selected && style.selectedBackgroundColor != null -> style.selectedBackgroundColor
            style.backgroundColor != null -> style.backgroundColor
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(300),
        label = "background_color"
    )

    val animatedElevation by animateDpAsState(
        targetValue = if (selected) style.cardElevation + 2.dp else style.cardElevation,
        animationSpec = tween(300),
        label = "elevation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                enabled = enabled,
                onClick = onClick,
                onLongClick = onLongClick
            )
            .semantics {
                role = Role.Button
            },
        colors = CardDefaults.cardColors(
            containerColor = animatedBackgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = animatedElevation),
        shape = style.cardShape ?: MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = style.horizontalPadding,
                    vertical = style.verticalPadding
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading Content (Custom or Default Icon)
            if (leadingContent != null) {
                leadingContent()
            } else {
                Box(
                    modifier = Modifier
                        .size(style.iconSize)
                        .clip(CircleShape)
                        .background(
                            style.iconBackgroundColor ?: MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(style.iconContentSize),
                        tint = style.iconTintColor ?: MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title and Subtitle
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = style.titleFontSize,
                        fontWeight = FontWeight.Normal,
                        color = if (enabled) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        },
                        style = style.titleTextStyle ?: LocalTextStyle.current,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    // Badge
                    if (badge != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(style.verticalSpacing))
                    Text(
                        text = subtitle,
                        fontSize = style.subtitleFontSize,
                        fontWeight = FontWeight.Normal,
                        color = if (enabled) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                        },
                        style = style.subtitleTextStyle ?: LocalTextStyle.current
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Custom Trailing Content
            if (trailingContent != null) {
                trailingContent()
            }

            // Trailing Arrow Icon
            if (hasTrailingIcon) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = stringResource(id = R.string.content_description_navigate),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Switch Button
            if (showSwitchesButton) {
                Switch(
                    checked = switchState,
                    onCheckedChange = { newState ->
                        onSwitchChange?.invoke(newState)
                    },
                    enabled = enabled
                )
            }
        }
    }
}

