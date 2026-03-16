package com.lifelog.services

import com.lifelog.database.DailyLogs
import com.lifelog.database.ExternalActivities
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object GitHubService {

    private val dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val githubToken = System.getenv("GITHUB_TOKEN") ?: ""
    private val githubUsername = System.getenv("GITHUB_USERNAME") ?: "Suisan-neki"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    /**
     * GitHub GraphQL APIからコントリビューション情報を取得し、
     * daily_log.commit_count を更新する
     */
    suspend fun syncContributions(): Int {
        if (githubToken.isBlank()) return 0

        val query = """
            query {
              user(login: "$githubUsername") {
                contributionsCollection {
                  contributionCalendar {
                    weeks {
                      contributionDays {
                        date
                        contributionCount
                      }
                    }
                  }
                }
              }
            }
        """.trimIndent()

        val response = client.post("https://api.github.com/graphql") {
            header(HttpHeaders.Authorization, "Bearer $githubToken")
            header(HttpHeaders.UserAgent, "LifeLog-Portfolio")
            contentType(ContentType.Application.Json)
            setBody(mapOf("query" to query))
        }

        val body = response.body<JsonObject>()
        val weeks = body["data"]
            ?.jsonObject?.get("user")
            ?.jsonObject?.get("contributionsCollection")
            ?.jsonObject?.get("contributionCalendar")
            ?.jsonObject?.get("weeks")
            ?.jsonArray ?: return 0

        var synced = 0

        transaction {
            for (week in weeks) {
                val days = week.jsonObject["contributionDays"]?.jsonArray ?: continue
                for (day in days) {
                    val date = day.jsonObject["date"]?.jsonPrimitive?.content ?: continue
                    val count = day.jsonObject["contributionCount"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0

                    if (count > 0) {
                        val existing = DailyLogs.selectAll()
                            .where { DailyLogs.date eq date }
                            .firstOrNull()

                        if (existing != null) {
                            DailyLogs.update({ DailyLogs.date eq date }) {
                                it[commitCount] = count
                            }
                        } else {
                            DailyLogs.insert {
                                it[DailyLogs.date] = date
                                it[swim] = false
                                it[study] = false
                                it[commitCount] = count
                                it[diaryId] = null
                            }
                        }
                        synced++
                    }
                }
            }
        }

        return synced
    }

    /**
     * GitHub APIから最近のイベントを取得してexternal_activityに保存する
     */
    suspend fun syncRecentActivity(): Int {
        if (githubToken.isBlank()) return 0

        val response = client.get("https://api.github.com/users/$githubUsername/events/public?per_page=30") {
            header(HttpHeaders.Authorization, "Bearer $githubToken")
            header(HttpHeaders.UserAgent, "LifeLog-Portfolio")
        }

        val events = response.body<List<JsonObject>>()
        var synced = 0

        transaction {
            ExternalActivities.deleteWhere { activitySource eq "github" }

            for (event in events.take(20)) {
                val type = event["type"]?.jsonPrimitive?.content ?: continue
                val repoName = event["repo"]?.jsonObject?.get("name")?.jsonPrimitive?.content ?: continue
                val repoUrl = "https://github.com/$repoName"
                val createdAt = event["created_at"]?.jsonPrimitive?.content ?: LocalDateTime.now().format(dtf)

                val titleStr = when (type) {
                    "PushEvent" -> "Push to $repoName"
                    "CreateEvent" -> "Created $repoName"
                    "PullRequestEvent" -> "PR in $repoName"
                    "IssuesEvent" -> "Issue in $repoName"
                    "ForkEvent" -> "Forked $repoName"
                    "WatchEvent" -> "Starred $repoName"
                    else -> "$type in $repoName"
                }

                ExternalActivities.insert {
                    it[activitySource] = "github"
                    it[title] = titleStr
                    it[url] = repoUrl
                    it[ExternalActivities.createdAt] = createdAt
                }
                synced++
            }
        }

        return synced
    }
}
