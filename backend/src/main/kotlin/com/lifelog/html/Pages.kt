package com.lifelog.html

import com.lifelog.models.*
import kotlinx.html.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

fun DIV.calendarContent(year: Int, month: Int, logs: List<DailyLog>) {
    val yearMonth = YearMonth.of(year, month)
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    // Day of week for first day: Monday=1 .. Sunday=7, we want Sunday=0 for 日月火水木金土 grid
    val firstDow = firstDay.dayOfWeek.value % 7  // Sunday=0, Monday=1, ..., Saturday=6
    val today = LocalDate.now()

    val prevMonth = yearMonth.minusMonths(1)
    val nextMonth = yearMonth.plusMonths(1)

    val logByDate = logs.associateBy { it.date }

    div(classes = "page-header") {
        div(classes = "page-title") { +"Calendar" }
        div(classes = "page-subtitle") { +"Daily activity log" }
    }

    div(classes = "calendar-nav") {
        a(href = "/?year=${prevMonth.year}&month=${prevMonth.monthValue}", classes = "btn") { +"← Prev" }
        h2 { +"${year}年 ${month}月" }
        a(href = "/?year=${nextMonth.year}&month=${nextMonth.monthValue}", classes = "btn") { +"Next →" }
    }

    div(classes = "calendar-grid") {
        val dayNames = listOf("日" to "sunday", "月" to "", "火" to "", "水" to "", "木" to "", "金" to "", "土" to "saturday")
        for ((name, cls) in dayNames) {
            div(classes = "calendar-header-cell${if (cls.isNotEmpty()) " $cls" else ""}") { +name }
        }

        // Empty cells before first day
        repeat(firstDow) {
            div(classes = "calendar-cell empty") {}
        }

        for (day in 1..daysInMonth) {
            val date = yearMonth.atDay(day)
            val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val log = logByDate[dateStr]
            val isToday = date == today
            val hasDiary = log?.diaryId != null
            val dow = date.dayOfWeek.value % 7 // 0=Sunday, 6=Saturday

            val cellClass = buildString {
                append("calendar-cell")
                if (isToday) append(" today")
                if (hasDiary) append(" has-diary")
                if (dow == 0) append(" sunday")
                if (dow == 6) append(" saturday")
            }

            div(classes = cellClass) {
                div(classes = "calendar-day") {
                    +day.toString()
                    if (hasDiary) {
                        span(classes = "diary-dot") {}
                    }
                }
                if (log != null) {
                    div(classes = "calendar-badges") {
                        if (log.swim) {
                            span(classes = "badge badge-swim") { +"swim" }
                        }
                        if (log.study) {
                            span(classes = "badge badge-study") { +"study" }
                        }
                        if (log.commitCount > 0) {
                            span(classes = "badge badge-commit") { +"★${log.commitCount}" }
                        }
                    }
                    if (hasDiary) {
                        a(href = "/diary/$dateStr", classes = "diary-link") { +"日記" }
                    }
                }
            }
        }
    }
}

fun DIV.projectsContent(projects: List<Project>) {
    div(classes = "page-header") {
        div(classes = "page-title") { +"Projects" }
        div(classes = "page-subtitle") { +"${projects.size} projects" }
    }

    if (projects.isEmpty()) {
        p { +"No projects yet." }
        return
    }

    for (project in projects) {
        div(classes = "card") {
            div(classes = "card-title") { +project.title }
            p(classes = "card-description") { +project.description }
            div {
                for (tag in project.tags) {
                    span(classes = "tag") { +tag }
                    +" "
                }
            }
            div(classes = "card-footer") {
                if (!project.githubUrl.isNullOrBlank()) {
                    a(href = project.githubUrl, classes = "btn") {
                        attributes["target"] = "_blank"
                        +"GitHub"
                    }
                }
                if (!project.demoUrl.isNullOrBlank()) {
                    a(href = project.demoUrl, classes = "btn btn-primary") {
                        attributes["target"] = "_blank"
                        +"Demo"
                    }
                }
                span(classes = "tag") { +project.createdAt.take(10) }
            }
        }
    }
}

fun DIV.goalsContent(goals: List<Goal>) {
    div(classes = "page-header") {
        div(classes = "page-title") { +"Goals" }
        div(classes = "page-subtitle") { +"${goals.size} goals" }
    }

    if (goals.isEmpty()) {
        p { +"No goals yet." }
        return
    }

    for (goal in goals) {
        val pct = if (goal.target > 0) (goal.progress * 100 / goal.target).coerceIn(0, 100) else 0
        div(classes = "card") {
            div(classes = "card-title") { +goal.title }
            if (goal.description.isNotBlank()) {
                p(classes = "card-description") { +goal.description }
            }
            div(classes = "progress-bar-container") {
                div(classes = "progress-bar-fill") {
                    style = "width: ${pct}%"
                }
            }
            div(classes = "progress-text") {
                +"${goal.progress} / ${goal.target} (${pct}%)"
            }
        }
    }
}

fun DIV.activityContent(activities: List<ExternalActivity>) {
    div(classes = "page-header") {
        div(classes = "page-title") { +"Activity" }
        div(classes = "page-subtitle") { +"Recent external activity" }
    }

    if (activities.isEmpty()) {
        p { +"No activity yet." }
        return
    }

    div(classes = "card") {
        for (activity in activities) {
            div(classes = "activity-item") {
                span(classes = "activity-source") { +activity.source }
                div(classes = "activity-title") {
                    a(href = activity.url) {
                        attributes["target"] = "_blank"
                        +activity.title
                    }
                }
                span(classes = "activity-date") { +activity.createdAt.take(10) }
            }
        }
    }
}

fun DIV.recordsContent(records: List<Record>) {
    val categories = listOf("event" to "Events", "conference" to "Conferences", "book" to "Books")

    div(classes = "page-header") {
        div(classes = "page-title") { +"Records" }
        div(classes = "page-subtitle") { +"${records.size} records" }
    }

    div(classes = "records-tabs") {
        a(href = "/records", classes = "records-tab active") { +"All" }
        for ((cat, label) in categories) {
            val count = records.count { it.category == cat }
            a(href = "/records?category=$cat", classes = "records-tab") { +"$label ($count)" }
        }
    }

    if (records.isEmpty()) {
        p { +"No records yet." }
        return
    }

    div(classes = "card") {
        for (record in records) {
            div(classes = "record-item") {
                span(classes = "record-date") { +record.date }
                div {
                    div(classes = "record-title") {
                        if (!record.url.isNullOrBlank()) {
                            a(href = record.url) {
                                attributes["target"] = "_blank"
                                +record.title
                            }
                        } else {
                            +record.title
                        }
                    }
                    if (record.note.isNotBlank()) {
                        div(classes = "record-note") { +record.note }
                    }
                }
                span(classes = "tag") { +record.category }
            }
        }
    }
}

fun DIV.adminContent(isAdmin: Boolean, diaries: List<Diary> = emptyList()) {
    if (!isAdmin) {
        script {
            unsafe { raw("window.location.href = '/login';") }
        }
        return
    }

    div(classes = "page-header") {
        div(classes = "page-title") { +"Admin" }
        div(classes = "page-subtitle") { +"Manage your portfolio content" }
        form(action = "/logout", method = FormMethod.post) {
            button(type = ButtonType.submit, classes = "btn") { +"Logout" }
        }
    }

    div(classes = "card") {
        div(classes = "card-title") { +"Diaries (${diaries.size})" }
        if (diaries.isEmpty()) {
            p(classes = "card-description") { +"No diaries yet." }
        } else {
            table(classes = "diary-table") {
                thead {
                    tr {
                        th { +"Date" }
                        th { +"Title" }
                        th { +"Created" }
                    }
                }
                tbody {
                    for (diary in diaries.sortedByDescending { it.date }) {
                        tr {
                            td { +diary.date }
                            td { +diary.title }
                            td { +diary.createdAt.take(10) }
                        }
                    }
                }
            }
        }
    }
}

fun DIV.loginContent(error: String? = null) {
    div(classes = "login-container") {
        div(classes = "page-header") {
            div(classes = "page-title") { +"Login" }
            div(classes = "page-subtitle") { +"Admin access required" }
        }

        if (error != null) {
            div(classes = "error-message") { +error }
        }

        form(action = "/login", method = FormMethod.post) {
            div(classes = "form-group") {
                label(classes = "form-label") {
                    attributes["for"] = "password"
                    +"Password"
                }
                passwordInput(classes = "form-input") {
                    id = "password"
                    name = "password"
                    placeholder = "Enter admin password"
                    attributes["autofocus"] = "true"
                }
            }
            button(type = ButtonType.submit, classes = "btn btn-primary") {
                style = "width: 100%"
                +"Login"
            }
        }
    }
}
