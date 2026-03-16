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
import com.lifelog.web.api.Diary
import com.lifelog.web.api.apiGetDiary
import com.lifelog.web.theme.*
import kotlinx.coroutines.launch

@Composable
fun DiaryPage(date: String, onBack: () -> Unit) {
    var diary by remember { mutableStateOf<Diary?>(null) }
    var loading by remember { mutableStateOf(true) }
    var notFound by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(date) {
        loading = true
        notFound = false
        diary = null
        scope.launch {
            try {
                diary = apiGetDiary(date)
            } catch (e: Exception) {
                notFound = true
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(BgTertiary)
                .clickable { onBack() }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "← 戻る",
                color = TextPrimary,
                fontSize = 14.sp
            )
        }

        if (loading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentBlue)
            }
        } else if (notFound || diary == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BgSecondary)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "日記が見つかりません",
                    color = TextSecondary,
                    fontSize = 16.sp
                )
            }
        } else {
            val entry = diary!!
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BgSecondary)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = entry.date,
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = entry.title,
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(BorderColor)
                )

                Text(
                    text = entry.contentMarkdown,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 26.sp
                )
            }
        }
    }
}
