package com.aqualogicasystem.izsu.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aqualogicasystem.izsu.R
import com.aqualogicasystem.izsu.ui.theme.IzsuAppTheme

@Composable
fun IzsuLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    Image(
        painter = painterResource(id = R.drawable.ic_logo),
        contentDescription = "İzsu Logo",
        modifier = modifier.size(size)
    )
}

@Preview(showBackground = true)
@Composable
fun IzsuLogoPreview() {
    IzsuAppTheme {
        IzsuLogo()
    }
}

