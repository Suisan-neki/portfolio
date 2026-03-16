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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifelog.web.api.Project
import com.lifelog.web.api.apiGetProjects
import com.lifelog.web.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProjectsPage() {
    var projects by remember { mutableStateOf<List<Project>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                projects = apiGetProjects()
            } catch (e: Exception) {
                projects = emptyList()
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
        Text(
            text = "Projects",
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
        } else if (projects.isEmpty()) {
            Text(
                text = "プロジェクトがありません",
                color = TextSecondary,
                fontSize = 14.sp
            )
        } else {
            projects.forEach { project ->
                ProjectCard(project = project)
            }
        }
    }
}

@Composable
private fun ProjectCard(project: Project) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BgSecondary)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = project.title,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = project.description,
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        if (project.tags.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                project.tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(BgTertiary)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tag,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        val githubUrl = project.githubUrl
        val demoUrl = project.demoUrl
        if (githubUrl != null || demoUrl != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (githubUrl != null) {
                    Text(
                        text = "GitHub",
                        color = AccentBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (demoUrl != null) {
                    Text(
                        text = "Demo",
                        color = AccentBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
