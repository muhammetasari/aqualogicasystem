package com.aqualogicasystem.izsu.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme

@Composable
fun ResultItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    unit: String,
    labelColor: Color = Color.White.copy(alpha = 0.8f),
    valueColor: Color = Color.White,
    unitColor: Color = Color.White.copy(alpha = 0.8f)
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = labelColor
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = unitColor,
                modifier = Modifier.paddingFromBaseline(bottom = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF8D6E63)
@Composable
fun ResultItemPreview() {
    IzsuAppTheme {
        ResultItem(
            label = "Tesis Giriş Debisi",
            value = "1250",
            unit = "lt/sn"
        )
    }
}

