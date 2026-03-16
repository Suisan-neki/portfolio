package com.lifelog.web.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.lifelog.web.api.DailyLog
import com.lifelog.web.api.apiGetCalendar
import com.lifelog.web.theme.*
import kotlinx.coroutines.launch

@JsFun("() => new Date().getFullYear()")
private external fun currentYear(): Int

@JsFun("() => new Date().getMonth() + 1")
private external fun currentMonth(): Int

private fun daysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> 30
    }
}

// Returns 0=Mon, 1=Tue, ..., 6=Sun
private fun firstDayOfWeek(year: Int, month: Int): Int {
    val y = if (month < 3) year - 1 else year
    val m = if (month < 3) month + 12 else month
    val k = y % 100
    val j = y / 100
    // Zeller's congruence (0=Sat, 1=Sun, 2=Mon, ..., 6=Fri)
    val h = (1 + (13 * (m + 1)) / 5 + k + k / 4 + j / 4 + 5 * j) % 7
    // Convert to Mon=0..Sun=6
    val dow = (h + 5) % 7
    return dow
}

private fun padded(n: Int): String = if (n < 10) "0$n" else "$n"

@Composable
fun CalendarPage(onDiaryClick: (String) -> Unit) {
    var year by remember { mutableStateOf(currentYear()) }
    var month by remember { mutableStateOf(currentMonth()) }
    var logs by remember { mutableStateOf<List<DailyLog>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun loadData(y: Int, m: Int) {
        scope.launch {
            loading = true
            try {
                logs = apiGetCalendar(y, m)
            } catch (e: Exception) {
                logs = emptyList()
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(year, month) {
        loadData(year, month)
    }

    val logMap = remember(logs) { logs.associateBy { it.date } }
    val days = daysInMonth(year, month)
    val firstDow = firstDayOfWeek(year, month)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(BgTertiary)
                    .clickable {
                        if (month == 1) { year -= 1; month = 12 } else { month -= 1 }
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("← 前月", color = TextPrimary, fontSize = 14.sp)
            }

            Text(
                text = "$year年 ${padded(month)}月",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(BgTertiary)
                    .clickable {
                        if (month == 12) { year += 1; month = 1 } else { month += 1 }
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("次月 →", color = TextPrimary, fontSize = 14.sp)
            }
        }

        if (loading) {
            Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentBlue)
            }
            return@Column
        }

        // Day-of-week headers
        val dowHeaders = listOf("月", "火", "水", "木", "金", "土", "日")
        Row(modifier = Modifier.fillMaxWidth()) {
            dowHeaders.forEach { label ->
                Box(
                    modifier = Modifier.weight(1f).padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Build calendar cells
        val totalCells = firstDow + days
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val day = cellIndex - firstDow + 1
                    if (day < 1 || day > days) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f).padding(4.dp))
                    } else {
                        val dateStr = "$year-${padded(month)}-${padded(day)}"
                        val log = logMap[dateStr]
                        val hasDiary = log?.diaryId != null
                        DayCell(
                            modifier = Modifier.weight(1f),
                            day = day,
                            log = log,
                            hasDiary = hasDiary,
                            onClick = if (hasDiary) ({ onDiaryClick(dateStr) }) else null
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LegendItem(color = AccentBlue, label = "水泳")
            LegendItem(color = AccentGreen, label = "勉強")
        }
    }
}

@Composable
private fun DayCell(
    modifier: Modifier,
    day: Int,
    log: DailyLog?,
    hasDiary: Boolean,
    onClick: (() -> Unit)?
) {
    val borderMod = if (hasDiary)
        Modifier.border(1.dp, AccentBlue.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
    else
        Modifier

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(BgSecondary)
            .then(borderMod)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Day number + commit badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "$day",
                    color = if (hasDiary) AccentBlue else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (hasDiary) FontWeight.Bold else FontWeight.Normal
                )
                val commits = log?.commitCount ?: 0
                if (commits > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AccentBlue.copy(alpha = 0.25f))
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "$commits",
                            color = AccentBlue,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Activity dots
            if (log != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    if (log.swim) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AccentBlue)
                        )
                    }
                    if (log.study) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AccentGreen)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
    }
}
