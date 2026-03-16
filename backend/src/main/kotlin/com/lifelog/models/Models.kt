package com.lifelog.models

import kotlinx.serialization.Serializable

@Serializable
data class DailyLog(
    val date: String,
    val swim: Boolean,
    val study: Boolean,
    val commitCount: Int,
    val diaryId: Int?
)

@Serializable
data class Diary(
    val id: Int,
    val date: String,
    val title: String,
    val contentMarkdown: String,
    val createdAt: String
)

@Serializable
data class Project(
    val id: Int,
    val title: String,
    val description: String,
    val tags: List<String>,
    val githubUrl: String?,
    val demoUrl: String?,
    val createdAt: String
)

@Serializable
data class Goal(
    val id: Int,
    val title: String,
    val description: String,
    val target: Int,
    val progress: Int,
    val createdAt: String
)

@Serializable
data class ExternalActivity(
    val id: Int,
    val source: String,
    val title: String,
    val url: String,
    val createdAt: String
)

// ── Request models ──────────────────────────────────────────────

@Serializable
data class CreateDiaryRequest(
    val date: String,
    val title: String,
    val contentMarkdown: String
)

@Serializable
data class UpdateRoutineRequest(
    val date: String,
    val swim: Boolean,
    val study: Boolean
)

@Serializable
data class CreateProjectRequest(
    val title: String,
    val description: String,
    val tags: List<String>,
    val githubUrl: String? = null,
    val demoUrl: String? = null
)

@Serializable
data class CreateGoalRequest(
    val title: String,
    val description: String = "",
    val target: Int = 10
)

@Serializable
data class UpdateGoalProgressRequest(
    val progress: Int
)

@Serializable
data class LoginRequest(
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String
)

@Serializable
data class SyncResponse(
    val synced: Int
)

@Serializable
data class ErrorResponse(
    val error: String
)

@Serializable
data class Record(
    val id: Int,
    val category: String,   // event | conference | book
    val title: String,
    val url: String?,
    val date: String,       // YYYY-MM-DD
    val note: String,
    val createdAt: String
)

@Serializable
data class CreateRecordRequest(
    val category: String,
    val title: String,
    val url: String? = null,
    val date: String,
    val note: String = ""
)
