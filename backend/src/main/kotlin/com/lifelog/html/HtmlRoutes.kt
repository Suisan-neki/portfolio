package com.lifelog.html

import com.lifelog.services.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate

fun Route.htmlRoutes() {

    // ── Calendar ──────────────────────────────────────────────────
    get("/") {
        val today = LocalDate.now()
        val year = call.request.queryParameters["year"]?.toIntOrNull() ?: today.year
        val month = call.request.queryParameters["month"]?.toIntOrNull() ?: today.monthValue
        val logs = DiaryService.getMonthlyLogs(year, month)
        call.respondHtml {
            portfolioLayout("calendar", "Calendar - Portfolio") {
                calendarContent(year, month, logs)
            }
        }
    }

    // ── Projects ──────────────────────────────────────────────────
    get("/projects") {
        val projects = ProjectService.getAllProjects()
        call.respondHtml {
            portfolioLayout("projects", "Projects - Portfolio") {
                projectsContent(projects)
            }
        }
    }

    // ── Goals ─────────────────────────────────────────────────────
    get("/goals") {
        val goals = GoalService.getAllGoals()
        call.respondHtml {
            portfolioLayout("goals", "Goals - Portfolio") {
                goalsContent(goals)
            }
        }
    }

    // ── Activity ──────────────────────────────────────────────────
    get("/activity") {
        val activities = ActivityService.getAllActivities()
        call.respondHtml {
            portfolioLayout("activity", "Activity - Portfolio") {
                activityContent(activities)
            }
        }
    }

    // ── Records ───────────────────────────────────────────────────
    get("/records") {
        val category = call.request.queryParameters["category"]
        val records = RecordService.getAllRecords(category)
        call.respondHtml {
            portfolioLayout("records", "Records - Portfolio") {
                recordsContent(records)
            }
        }
    }

    // ── Admin ─────────────────────────────────────────────────────
    get("/admin") {
        val adminToken = System.getenv("ADMIN_TOKEN") ?: "lifelog-admin-token"
        val isAdmin = call.request.cookies["admin_token"] == adminToken
        if (!isAdmin) {
            call.respondRedirect("/login")
            return@get
        }
        val diaries = DiaryService.getAllDiaries()
        call.respondHtml {
            portfolioLayout("admin", "Admin - Portfolio") {
                adminContent(isAdmin = true, diaries = diaries)
            }
        }
    }

    // ── Login ─────────────────────────────────────────────────────
    get("/login") {
        val adminToken = System.getenv("ADMIN_TOKEN") ?: "lifelog-admin-token"
        val isAdmin = call.request.cookies["admin_token"] == adminToken
        if (isAdmin) {
            call.respondRedirect("/admin")
            return@get
        }
        call.respondHtml {
            portfolioLayout("login", "Login - Portfolio") {
                loginContent()
            }
        }
    }

    post("/login") {
        val params = call.receiveParameters()
        val password = params["password"] ?: ""
        val expectedPassword = System.getenv("ADMIN_PASSWORD") ?: "lifelog2024"
        val adminToken = System.getenv("ADMIN_TOKEN") ?: "lifelog-admin-token"

        if (password == expectedPassword) {
            call.response.cookies.append(
                Cookie(
                    name = "admin_token",
                    value = adminToken,
                    path = "/",
                    httpOnly = true,
                    maxAge = 60 * 60 * 24 * 30  // 30 days
                )
            )
            call.respondRedirect("/admin")
        } else {
            call.respondHtml(HttpStatusCode.Unauthorized) {
                portfolioLayout("login", "Login - Portfolio") {
                    loginContent(error = "Invalid password. Please try again.")
                }
            }
        }
    }

    // ── Logout ────────────────────────────────────────────────────
    post("/logout") {
        call.response.cookies.append(
            Cookie(
                name = "admin_token",
                value = "",
                path = "/",
                httpOnly = true,
                maxAge = 0
            )
        )
        call.respondRedirect("/")
    }
}
