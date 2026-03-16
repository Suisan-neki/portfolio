package com.lifelog.web.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifelog.web.Screen
import com.lifelog.web.theme.*

data class NavItem(val label: String, val screen: Screen)

private val navItems = listOf(
    NavItem("Calendar",  Screen.Calendar),
    NavItem("Projects",  Screen.Projects),
    NavItem("Goals",     Screen.Goals),
    NavItem("Activity",  Screen.Activity),
    NavItem("Records",   Screen.Records),
    NavItem("Admin",     Screen.Admin),
)

@Composable
fun MainLayout(
    current: Screen,
    onNavigate: (Screen) -> Unit,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
    ) {
        // ── Sidebar ──────────────────────────────────────────────
        Column(
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
                .background(BgSecondary)
                .padding(horizontal = 12.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Suisan",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
            Text(
                text = "Portfolio",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 8.dp).padding(bottom = 16.dp)
            )

            navItems.forEach { item ->
                val isActive = item.screen::class == current::class
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isActive) BgTertiary else BgSecondary)
                        .clickable { onNavigate(item.screen) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = item.label,
                        fontSize = 14.sp,
                        color = if (isActive) TextPrimary else TextSecondary,
                        fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
                    )
                }
                Spacer(Modifier.height(2.dp))
            }
        }

        // ── Main content ─────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgPrimary)
        ) {
            content()
        }
    }
}
