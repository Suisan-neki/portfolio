package com.lifelog.web.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifelog.web.api.*
import com.lifelog.web.theme.*
import kotlinx.browser.localStorage
import kotlinx.coroutines.launch

@Composable
fun AdminPage() {
    var isLoggedIn by remember {
        mutableStateOf(localStorage.getItem("admin_token") != null)
    }

    if (isLoggedIn) {
        AdminDashboard(onLogout = {
            localStorage.removeItem("admin_token")
            isLoggedIn = false
        })
    } else {
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
    }
}

@Composable
private fun LoginScreen(onLoginSuccess: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "Admin Login",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it; errorMsg = "" },
            placeholder = { Text("パスワード", color = TextSecondary) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = BgSecondary,
                unfocusedContainerColor = BgSecondary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = AccentBlue,
                unfocusedIndicatorColor = BorderColor,
                cursorColor = AccentBlue
            )
        )

        if (errorMsg.isNotEmpty()) {
            Text(
                text = errorMsg,
                color = AccentRed,
                fontSize = 14.sp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(AccentBlue)
                .clickable(enabled = !loading) {
                    scope.launch {
                        loading = true
                        try {
                            val response = apiLogin(password)
                            localStorage.setItem("admin_token", response.token)
                            onLoginSuccess()
                        } catch (e: Exception) {
                            errorMsg = "パスワードが違います"
                        } finally {
                            loading = false
                        }
                    }
                }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = BgPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "ログイン",
                    color = BgPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AdminDashboard(onLogout: () -> Unit) {
    var syncCount by remember { mutableStateOf<Int?>(null) }
    var syncLoading by remember { mutableStateOf(false) }

    // Records state
    var records by remember { mutableStateOf<List<Record>>(emptyList()) }
    var recordsLoading by remember { mutableStateOf(true) }
    var recCategory by remember { mutableStateOf("event") }
    var recDate by remember { mutableStateOf("") }
    var recTitle by remember { mutableStateOf("") }
    var recUrl by remember { mutableStateOf("") }
    var recNote by remember { mutableStateOf("") }
    var recSaving by remember { mutableStateOf(false) }

    // Projects state
    var projects by remember { mutableStateOf<List<Project>>(emptyList()) }
    var projectsLoading by remember { mutableStateOf(true) }
    var projTitle by remember { mutableStateOf("") }
    var projDesc by remember { mutableStateOf("") }
    var projTags by remember { mutableStateOf("") }
    var projGithub by remember { mutableStateOf("") }
    var projDemo by remember { mutableStateOf("") }
    var projSaving by remember { mutableStateOf(false) }

    // Goals state
    var goals by remember { mutableStateOf<List<Goal>>(emptyList()) }
    var goalsLoading by remember { mutableStateOf(true) }
    var goalTitle by remember { mutableStateOf("") }
    var goalDesc by remember { mutableStateOf("") }
    var goalTarget by remember { mutableStateOf("10") }
    var goalSaving by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun loadRecords() {
        scope.launch {
            recordsLoading = true
            try { records = apiGetRecords() } catch (_: Exception) {}
            recordsLoading = false
        }
    }

    fun loadProjects() {
        scope.launch {
            projectsLoading = true
            try { projects = apiGetProjects() } catch (_: Exception) {}
            projectsLoading = false
        }
    }

    fun loadGoals() {
        scope.launch {
            goalsLoading = true
            try { goals = apiGetGoals() } catch (_: Exception) {}
            goalsLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadRecords()
        loadProjects()
        loadGoals()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Admin Dashboard",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentRed.copy(alpha = 0.15f))
                    .clickable { onLogout() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("ログアウト", color = AccentRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        // GitHub Sync section
        SectionCard(title = "GitHub同期") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminButton(
                    label = "同期する",
                    loading = syncLoading,
                    color = AccentBlue
                ) {
                    scope.launch {
                        syncLoading = true
                        try {
                            val res = apiSyncGitHub()
                            syncCount = res.synced
                        } catch (_: Exception) {}
                        syncLoading = false
                    }
                }
                val count = syncCount
                if (count != null) {
                    Text(
                        text = "${count}件同期しました",
                        color = AccentGreen,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Records section
        SectionCard(title = "Records") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Category select
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("event", "conference", "book").forEach { cat ->
                        val catLabel = when (cat) { "event" -> "イベント"; "conference" -> "カンファレンス"; else -> "技術書" }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (recCategory == cat) AccentBlue.copy(alpha = 0.2f) else BgTertiary)
                                .clickable { recCategory = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(catLabel, color = if (recCategory == cat) AccentBlue else TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
                AdminTextField("日付 (YYYY-MM-DD)", recDate) { recDate = it }
                AdminTextField("タイトル", recTitle) { recTitle = it }
                AdminTextField("URL (任意)", recUrl) { recUrl = it }
                AdminTextField("メモ (任意)", recNote) { recNote = it }
                AdminButton(label = "追加", loading = recSaving, color = AccentGreen) {
                    if (recTitle.isNotBlank() && recDate.isNotBlank()) {
                        scope.launch {
                            recSaving = true
                            try {
                                apiCreateRecord(CreateRecordRequest(
                                    category = recCategory,
                                    title = recTitle,
                                    url = recUrl.ifBlank { null },
                                    date = recDate,
                                    note = recNote
                                ))
                                recTitle = ""; recDate = ""; recUrl = ""; recNote = ""
                                loadRecords()
                            } catch (_: Exception) {}
                            recSaving = false
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                if (recordsLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AccentBlue)
                } else {
                    records.forEach { record ->
                        AdminListItem(
                            title = record.title,
                            subtitle = "${record.category} · ${record.date}"
                        ) {
                            scope.launch {
                                try { apiDeleteRecord(record.id); loadRecords() } catch (_: Exception) {}
                            }
                        }
                    }
                }
            }
        }

        // Projects section
        SectionCard(title = "Projects") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminTextField("タイトル", projTitle) { projTitle = it }
                AdminTextField("説明", projDesc) { projDesc = it }
                AdminTextField("タグ (カンマ区切り)", projTags) { projTags = it }
                AdminTextField("GitHub URL (任意)", projGithub) { projGithub = it }
                AdminTextField("Demo URL (任意)", projDemo) { projDemo = it }
                AdminButton(label = "追加", loading = projSaving, color = AccentGreen) {
                    if (projTitle.isNotBlank()) {
                        scope.launch {
                            projSaving = true
                            try {
                                val tags = projTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                apiCreateProject(CreateProjectRequest(
                                    title = projTitle,
                                    description = projDesc,
                                    tags = tags,
                                    githubUrl = projGithub.ifBlank { null },
                                    demoUrl = projDemo.ifBlank { null }
                                ))
                                projTitle = ""; projDesc = ""; projTags = ""; projGithub = ""; projDemo = ""
                                loadProjects()
                            } catch (_: Exception) {}
                            projSaving = false
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                if (projectsLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AccentBlue)
                } else {
                    projects.forEach { project ->
                        AdminListItem(
                            title = project.title,
                            subtitle = project.tags.joinToString(", ")
                        ) {
                            scope.launch {
                                try { apiDeleteProject(project.id); loadProjects() } catch (_: Exception) {}
                            }
                        }
                    }
                }
            }
        }

        // Goals section
        SectionCard(title = "Goals") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminTextField("タイトル", goalTitle) { goalTitle = it }
                AdminTextField("説明", goalDesc) { goalDesc = it }
                AdminTextField("目標値", goalTarget) { goalTarget = it }
                AdminButton(label = "追加", loading = goalSaving, color = AccentGreen) {
                    if (goalTitle.isNotBlank()) {
                        scope.launch {
                            goalSaving = true
                            try {
                                apiCreateGoal(CreateGoalRequest(
                                    title = goalTitle,
                                    description = goalDesc,
                                    target = goalTarget.toIntOrNull() ?: 10
                                ))
                                goalTitle = ""; goalDesc = ""; goalTarget = "10"
                                loadGoals()
                            } catch (_: Exception) {}
                            goalSaving = false
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                if (goalsLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AccentBlue)
                } else {
                    goals.forEach { goal ->
                        AdminListItem(
                            title = goal.title,
                            subtitle = "${goal.progress} / ${goal.target}"
                        ) {
                            scope.launch {
                                try { apiDeleteGoal(goal.id); loadGoals() } catch (_: Exception) {}
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BgSecondary)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderColor)
        )
        content()
    }
}

@Composable
private fun AdminTextField(
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = TextSecondary, fontSize = 13.sp) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = BgTertiary,
            unfocusedContainerColor = BgTertiary,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedIndicatorColor = AccentBlue,
            unfocusedIndicatorColor = BorderColor,
            cursorColor = AccentBlue
        )
    )
}

@Composable
private fun AdminButton(
    label: String,
    loading: Boolean,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color, RoundedCornerShape(8.dp))
            .clickable(enabled = !loading) { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = color,
                strokeWidth = 2.dp
            )
        } else {
            Text(label, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AdminListItem(
    title: String,
    subtitle: String,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(BgTertiary)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(AccentRed.copy(alpha = 0.15f))
                .clickable { onDelete() }
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text("削除", color = AccentRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
