package com.lifelog.web.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifelog.web.api.Record
import com.lifelog.web.api.apiGetRecords
import com.lifelog.web.theme.*
import kotlinx.coroutines.launch

private val tabs = listOf(
    "all" to "All",
    "event" to "イベント",
    "conference" to "カンファレンス",
    "book" to "技術書"
)

@Composable
fun RecordsPage() {
    var records by remember { mutableStateOf<List<Record>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf("all") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                records = apiGetRecords()
            } catch (e: Exception) {
                records = emptyList()
            } finally {
                loading = false
            }
        }
    }

    val filteredRecords = remember(records, selectedTab) {
        if (selectedTab == "all") records
        else records.filter { it.category == selectedTab }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Records",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Category tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEach { (key, label) ->
                val isSelected = selectedTab == key
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) AccentBlue.copy(alpha = 0.2f) else BgTertiary)
                        .clickable { selectedTab = key }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) AccentBlue else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        if (loading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentBlue)
            }
        } else if (filteredRecords.isEmpty()) {
            Text(
                text = "レコードがありません",
                color = TextSecondary,
                fontSize = 14.sp
            )
        } else {
            filteredRecords.forEach { record ->
                RecordCard(record = record)
            }
        }
    }
}

@Composable
private fun RecordCard(record: Record) {
    val (badgeColor, badgeBg) = when (record.category) {
        "event" -> Pair(AccentBlue, AccentBlue.copy(alpha = 0.15f))
        "conference" -> Pair(AccentPurple, AccentPurple.copy(alpha = 0.15f))
        "book" -> Pair(AccentGreen, AccentGreen.copy(alpha = 0.15f))
        else -> Pair(TextSecondary, BgTertiary)
    }

    val categoryLabel = when (record.category) {
        "event" -> "イベント"
        "conference" -> "カンファレンス"
        "book" -> "技術書"
        else -> record.category
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BgSecondary)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = categoryLabel,
                    color = badgeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = record.date,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Text(
            text = record.title,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 22.sp
        )

        if (record.note.isNotEmpty()) {
            Text(
                text = record.note,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}
