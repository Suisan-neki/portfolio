package com.lifelog.web.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifelog.web.api.ExternalActivity
import com.lifelog.web.api.apiGetActivity
import com.lifelog.web.theme.*
import kotlinx.coroutines.launch

@Composable
fun ActivityPage() {
    var activities by remember { mutableStateOf<List<ExternalActivity>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                activities = apiGetActivity()
            } catch (e: Exception) {
                activities = emptyList()
            } finally {
                loading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Activity",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (loading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentBlue)
            }
        } else if (activities.isEmpty()) {
            Text(
                text = "アクティビティがありません",
                color = TextSecondary,
                fontSize = 14.sp
            )
        } else {
            activities.forEach { activity ->
                ActivityCard(activity = activity)
            }
        }
    }
}

@Composable
private fun ActivityCard(activity: ExternalActivity) {
    val (badgeColor, badgeBg) = when (activity.source.lowercase()) {
        "github" -> Pair(AccentBlue, AccentBlue.copy(alpha = 0.15f))
        "qiita" -> Pair(AccentGreen, AccentGreen.copy(alpha = 0.15f))
        else -> Pair(TextSecondary, BgTertiary)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BgSecondary)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(badgeBg)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = activity.source,
                color = badgeColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = activity.title,
                color = AccentBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp
            )
            Text(
                text = activity.createdAt.take(10),
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}
