package com.lifelog.web.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json

// API_BASE_URL は build.gradle.kts がビルド時に生成する ApiConfig.kt の定数
// ローカル: "" → /api (バックエンドを localhost:8080 で直接起動)
// 本番: "https://xxx.up.railway.app" → https://xxx.up.railway.app/api
private val baseUrl get() = if (API_BASE_URL.isNotEmpty()) "${API_BASE_URL}/api" else "/api"

val httpClient = HttpClient(Js) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

private fun HttpRequestBuilder.withAuth() {
    val token = localStorage.getItem("admin_token")
    if (token != null) header(HttpHeaders.Authorization, "Bearer $token")
}

// ── Auth ──────────────────────────────────────────────────────────

suspend fun apiLogin(password: String): LoginResponse =
    httpClient.post("$baseUrl/auth/login") {
        contentType(ContentType.Application.Json)
        setBody(LoginRequest(password))
    }.body()

// ── Calendar ──────────────────────────────────────────────────────

suspend fun apiGetCalendar(year: Int, month: Int): List<DailyLog> =
    httpClient.get("$baseUrl/calendar/$year/$month").body()

// ── Diary ─────────────────────────────────────────────────────────

suspend fun apiGetDiary(date: String): Diary =
    httpClient.get("$baseUrl/diary/$date").body()

// ── Projects ──────────────────────────────────────────────────────

suspend fun apiGetProjects(): List<Project> =
    httpClient.get("$baseUrl/projects").body()

suspend fun apiCreateProject(req: CreateProjectRequest): Project =
    httpClient.post("$baseUrl/projects") {
        contentType(ContentType.Application.Json)
        setBody(req)
        withAuth()
    }.body()

suspend fun apiDeleteProject(id: Int) =
    httpClient.delete("$baseUrl/projects/$id") { withAuth() }

// ── Goals ─────────────────────────────────────────────────────────

suspend fun apiGetGoals(): List<Goal> =
    httpClient.get("$baseUrl/goals").body()

suspend fun apiCreateGoal(req: CreateGoalRequest): Goal =
    httpClient.post("$baseUrl/goals") {
        contentType(ContentType.Application.Json)
        setBody(req)
        withAuth()
    }.body()

suspend fun apiUpdateGoalProgress(id: Int, progress: Int): Goal =
    httpClient.put("$baseUrl/goals/$id/progress") {
        contentType(ContentType.Application.Json)
        setBody(UpdateGoalProgressRequest(progress))
        withAuth()
    }.body()

suspend fun apiDeleteGoal(id: Int) =
    httpClient.delete("$baseUrl/goals/$id") { withAuth() }

// ── Activity ──────────────────────────────────────────────────────

suspend fun apiGetActivity(): List<ExternalActivity> =
    httpClient.get("$baseUrl/activity").body()

// ── Records ───────────────────────────────────────────────────────

suspend fun apiGetRecords(category: String? = null): List<Record> =
    httpClient.get("$baseUrl/records") {
        if (category != null) parameter("category", category)
    }.body()

suspend fun apiCreateRecord(req: CreateRecordRequest): Record =
    httpClient.post("$baseUrl/records") {
        contentType(ContentType.Application.Json)
        setBody(req)
        withAuth()
    }.body()

suspend fun apiDeleteRecord(id: Int) =
    httpClient.delete("$baseUrl/records/$id") { withAuth() }

// ── GitHub Sync ───────────────────────────────────────────────────

suspend fun apiSyncGitHub(): SyncResponse =
    httpClient.post("$baseUrl/github/sync") { withAuth() }.body()
