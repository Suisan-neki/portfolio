package com.lifelog.plugins

import com.lifelog.models.*
import com.lifelog.services.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/api") {

            // ── Auth ──────────────────────────────────────────────
            post("/auth/login") {
                val req = call.receive<LoginRequest>()
                val expectedPassword = System.getenv("ADMIN_PASSWORD") ?: "lifelog2024"
                if (req.password == expectedPassword) {
                    val token = System.getenv("ADMIN_TOKEN") ?: "lifelog-admin-token"
                    call.respond(LoginResponse(token))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid password"))
                }
            }

            // ── Calendar ──────────────────────────────────────────
            get("/calendar/{year}/{month}") {
                val year = call.parameters["year"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid year"))
                val month = call.parameters["month"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid month"))
                val logs = DiaryService.getMonthlyLogs(year, month)
                call.respond(logs)
            }

            // ── Diary ─────────────────────────────────────────────
            get("/diary") {
                call.respond(DiaryService.getAllDiaries())
            }

            get("/diary/{date}") {
                val date = call.parameters["date"]
                    ?: return@get call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid date"))
                val diary = DiaryService.getDiary(date)
                    ?: return@get call.respond(HttpStatusCode.NotFound, ErrorResponse("Diary not found"))
                call.respond(diary)
            }

            authenticate("admin-auth") {
                post("/diary") {
                    val req = call.receive<CreateDiaryRequest>()
                    val diary = DiaryService.createDiary(req)
                    call.respond(HttpStatusCode.Created, diary)
                }

                put("/diary/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid id"))
                    val req = call.receive<CreateDiaryRequest>()
                    val diary = DiaryService.updateDiary(id, req)
                    call.respond(diary)
                }

                delete("/diary/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid id"))
                    DiaryService.deleteDiary(id)
                    call.respond(HttpStatusCode.NoContent)
                }

                // ── Routine ───────────────────────────────────────
                put("/routine") {
                    val req = call.receive<UpdateRoutineRequest>()
                    val log = RoutineService.updateRoutine(req)
                    call.respond(log)
                }
            }

            // ── Projects ──────────────────────────────────────────
            get("/projects") {
                call.respond(ProjectService.getAllProjects())
            }

            authenticate("admin-auth") {
                post("/projects") {
                    val req = call.receive<CreateProjectRequest>()
                    val project = ProjectService.createProject(req)
                    call.respond(HttpStatusCode.Created, project)
                }

                put("/projects/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid id"))
                    val req = call.receive<CreateProjectRequest>()
                    val project = ProjectService.updateProject(id, req)
                    call.respond(project)
                }

                delete("/projects/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid id"))
                    ProjectService.deleteProject(id)
                    call.respond(HttpStatusCode.NoContent)
                }
            }

            // ── Goals ─────────────────────────────────────────────
            get("/goals") {
                call.respond(GoalService.getAllGoals())
            }

            authenticate("admin-auth") {
                post("/goals") {
                    val req = call.receive<CreateGoalRequest>()
                    val goal = GoalService.createGoal(req)
                    call.respond(HttpStatusCode.Created, goal)
                }

                put("/goals/{id}/progress") {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid id"))
                    val req = call.receive<UpdateGoalProgressRequest>()
                    val goal = GoalService.updateProgress(id, req)
                    call.respond(goal)
                }

                delete("/goals/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid id"))
                    GoalService.deleteGoal(id)
                    call.respond(HttpStatusCode.NoContent)
                }
            }

            // ── Activity ──────────────────────────────────────────
            get("/activity") {
                call.respond(ActivityService.getAllActivities())
            }

            // ── Records ───────────────────────────────────────────
            get("/records") {
                val category = call.request.queryParameters["category"]
                call.respond(RecordService.getAllRecords(category))
            }

            authenticate("admin-auth") {
                post("/records") {
                    val req = call.receive<CreateRecordRequest>()
                    val record = RecordService.createRecord(req)
                    call.respond(HttpStatusCode.Created, record)
                }

                put("/records/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: return@put call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid id"))
                    val req = call.receive<CreateRecordRequest>()
                    val record = RecordService.updateRecord(id, req)
                    call.respond(record)
                }

                delete("/records/{id}") {
                    val id = call.parameters["id"]?.toIntOrNull()
                        ?: return@delete call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid id"))
                    RecordService.deleteRecord(id)
                    call.respond(HttpStatusCode.NoContent)
                }
            }

            // ── GitHub Sync ───────────────────────────────────────
            authenticate("admin-auth") {
                post("/github/sync") {
                    val synced = GitHubService.syncContributions()
                    GitHubService.syncRecentActivity()
                    call.respond(SyncResponse(synced))
                }
            }
        }

        // フロントエンド静的ファイルの配信 (SPA フォールバック付き)
        staticResources("/", "static")
        get("{...}") {
            call.respondRedirect("/")
        }
    }
}
