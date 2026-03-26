package com.lifelog.web.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

// GitHub ダークテーマ風カラー
val BgPrimary    = Color(0xFF0d1117)
val BgSecondary  = Color(0xFF161b22)
val BgTertiary   = Color(0xFF21262d)
val BorderColor  = Color(0xFF30363d)
val TextPrimary  = Color(0xFFe6edf3)
val TextSecondary = Color(0xFF8b949e)
val AccentBlue   = Color(0xFF58a6ff)
val AccentGreen  = Color(0xFF3fb950)
val AccentPurple = Color(0xFFbc8cff)
val AccentRed    = Color(0xFFf78166)

private val NotoSansJP = FontFamily("Noto Sans JP")

private val DarkColors = darkColorScheme(
    primary         = AccentBlue,
    onPrimary       = BgPrimary,
    background      = BgPrimary,
    onBackground    = TextPrimary,
    surface         = BgSecondary,
    onSurface       = TextPrimary,
    surfaceVariant  = BgTertiary,
    onSurfaceVariant = TextSecondary,
    outline         = BorderColor,
    secondary       = AccentGreen,
    onSecondary     = BgPrimary,
    error           = AccentRed,
    onError         = BgPrimary,
)

@Composable
fun PortfolioTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = androidx.compose.material3.Typography(
            bodyLarge   = androidx.compose.ui.text.TextStyle(fontFamily = NotoSansJP),
            bodyMedium  = androidx.compose.ui.text.TextStyle(fontFamily = NotoSansJP),
            bodySmall   = androidx.compose.ui.text.TextStyle(fontFamily = NotoSansJP),
            labelLarge  = androidx.compose.ui.text.TextStyle(fontFamily = NotoSansJP),
            labelMedium = androidx.compose.ui.text.TextStyle(fontFamily = NotoSansJP),
            titleLarge  = androidx.compose.ui.text.TextStyle(fontFamily = NotoSansJP),
            titleMedium = androidx.compose.ui.text.TextStyle(fontFamily = NotoSansJP),
            titleSmall  = androidx.compose.ui.text.TextStyle(fontFamily = NotoSansJP),
        ),
        content = content,
    )
}
