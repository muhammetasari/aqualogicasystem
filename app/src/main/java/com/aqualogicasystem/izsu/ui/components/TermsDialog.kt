package com.aqualogicasystem.izsu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.aqualogicasystem.izsu.R
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme

/**
 * Generic bilgi dialog componenti
 * @param title Dialog başlığı
 * @param content Dialog içeriği
 * @param onDismiss Dialog kapanma callback'i
 */
@Composable
private fun InfoDialog(
    title: String,
    content: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.close))
            }
        }
    )
}

@Composable
fun TermsOfUseDialog(
    onDismiss: () -> Unit
) {
    InfoDialog(
        title = stringResource(id = R.string.terms_of_use_title),
        content = stringResource(id = R.string.terms_of_use_content),
        onDismiss = onDismiss
    )
}

@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit
) {
    InfoDialog(
        title = stringResource(id = R.string.privacy_policy_title),
        content = stringResource(id = R.string.privacy_policy_content),
        onDismiss = onDismiss
    )
}



@Composable
fun TermsPrivacyText(
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val termsText = stringResource(id = R.string.terms_of_use)
    val privacyText = stringResource(id = R.string.privacy_policy)
    val fullText = stringResource(id = R.string.terms_privacy_text)

    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    val annotatedString = buildAnnotatedString {
        val termsStart = fullText.indexOf(termsText)
        val privacyStart = fullText.indexOf(privacyText)

        if (termsStart < 0 || privacyStart < 0) {
            withStyle(style = SpanStyle(color = textColor)) {
                append(fullText)
            }
            return@buildAnnotatedString
        }

        // Başlangıç metni
        withStyle(style = SpanStyle(color = textColor)) {
            append(fullText.substring(0, termsStart))
        }

        // Kullanım Koşulları linki
        withLink(
            LinkAnnotation.Clickable(
                tag = "TERMS",
                linkInteractionListener = { onTermsClick() }
            )
        ) {
            withStyle(
                style = SpanStyle(
                    color = primaryColor,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(termsText)
            }
        }

        // Ara metin
        withStyle(style = SpanStyle(color = textColor)) {
            append(fullText.substring(termsStart + termsText.length, privacyStart))
        }

        // Gizlilik Politikası linki
        withLink(
            LinkAnnotation.Clickable(
                tag = "PRIVACY",
                linkInteractionListener = { onPrivacyClick() }
            )
        ) {
            withStyle(
                style = SpanStyle(
                    color = primaryColor,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(privacyText)
            }
        }

        // Son metin
        withStyle(style = SpanStyle(color = textColor)) {
            append(fullText.substring(privacyStart + privacyText.length))
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun TermsPrivacyTextPreview() {
    IzsuAppTheme {
        TermsPrivacyText(
            onTermsClick = {},
            onPrivacyClick = {}
        )
    }
}
@Preview(showBackground = true)
@Composable
fun TermsOfUseDialogPreview() {
    IzsuAppTheme {
        TermsOfUseDialog(onDismiss = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacyPolicyDialogPreview() {
    IzsuAppTheme {
        PrivacyPolicyDialog(onDismiss = {})
    }
}
