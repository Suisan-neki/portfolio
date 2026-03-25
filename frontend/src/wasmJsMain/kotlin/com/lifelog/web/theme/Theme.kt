package com.lifelog.web.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography
import lifelog.frontend.generated.resources.Res
import lifelog.frontend.generated.resources.NotoSansJP
import org.jetbrains.compose.resources.Font

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
fun notoSansJP() = FontFamily(Font(Res.font.NotoSansJP))

@Composable
fun PortfolioTheme(content: @Composable () -> Unit) {
    val fontFamily = notoSansJP()
    val typography = Typography(
        bodyLarge   = TextStyle(fontFamily = fontFamily, fontSize = 16.sp),
        bodyMedium  = TextStyle(fontFamily = fontFamily, fontSize = 14.sp),
        bodySmall   = TextStyle(fontFamily = fontFamily, fontSize = 12.sp),
        labelLarge  = TextStyle(fontFamily = fontFamily, fontSize = 14.sp),
        labelMedium = TextStyle(fontFamily = fontFamily, fontSize = 12.sp),
        titleLarge  = TextStyle(fontFamily = fontFamily, fontSize = 22.sp),
        titleMedium = TextStyle(fontFamily = fontFamily, fontSize = 16.sp),
        titleSmall  = TextStyle(fontFamily = fontFamily, fontSize = 14.sp),
    )
    MaterialTheme(
        colorScheme = DarkColors,
        typography = typography,
        content = content,
    )
}
