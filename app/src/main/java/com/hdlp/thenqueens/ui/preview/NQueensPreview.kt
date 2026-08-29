package com.hdlp.thenqueens.ui.preview

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.hdlp.thenqueens.ui.theme.TheNQueensTheme

private const val SMALL_PHONE = "spec:width=320dp,height=568dp,dpi=320"
private const val PHONE_LANDSCAPE = "spec:width=891dp,height=411dp"

@Preview(name = "small phone", group = "light", device = SMALL_PHONE, showSystemUi = true)
@Preview(name = "phone", group = "light", device = Devices.PHONE, showSystemUi = true)
@Preview(name = "phone landscape", group = "light", device = PHONE_LANDSCAPE, showSystemUi = true)
@Preview(name = "tablet", group = "light", device = Devices.TABLET, showSystemUi = true)
@Preview(name = "foldable", group = "light", device = Devices.FOLDABLE, showSystemUi = true)
@Preview(name = "desktop", group = "light", device = Devices.DESKTOP, showSystemUi = true)
@Preview(
    name = "small phone (dark)",
    group = "dark",
    device = SMALL_PHONE,
    uiMode = UI_MODE_NIGHT_YES,
    showSystemUi = true,
)
@Preview(
    name = "phone (dark)",
    group = "dark",
    device = Devices.PHONE,
    uiMode = UI_MODE_NIGHT_YES,
    showSystemUi = true,
)
@Preview(
    name = "phone landscape (dark)",
    group = "dark",
    device = PHONE_LANDSCAPE,
    uiMode = UI_MODE_NIGHT_YES,
    showSystemUi = true,
)
@Preview(
    name = "tablet (dark)",
    group = "dark",
    device = Devices.TABLET,
    uiMode = UI_MODE_NIGHT_YES,
    showSystemUi = true,
)
annotation class NQueensPreview

// Annotations can't wrap composables, so the device/theme matrix above pairs with this
// wrapper, which mirrors the chrome MainActivity puts around NQueensApp.
@Composable
fun NQueensPreviewSurface(content: @Composable () -> Unit) {
    TheNQueensTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            content()
        }
    }
}
