package com.lifelog.html

import io.ktor.server.html.*
import kotlinx.html.*

fun HTML.portfolioLayout(currentPage: String, pageTitle: String = "Portfolio", content: DIV.() -> Unit) {
    head {
        meta(charset = "UTF-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
        title(pageTitle)
        link(rel = "preconnect", href = "https://fonts.googleapis.com")
        link(rel = "preconnect", href = "https://fonts.gstatic.com") {
            attributes["crossorigin"] = ""
        }
        link(
            rel = "stylesheet",
            href = "https://fonts.googleapis.com/css2?family=Noto+Sans+JP:wght@400;500;700&display=swap"
        )
        script(src = "https://unpkg.com/htmx.org@1.9.10") {}
        style {
            unsafe {
                raw("""
                    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

                    :root {
                        --bg: #0d1117;
                        --secondary: #161b22;
                        --tertiary: #21262d;
                        --border: #30363d;
                        --text: #e6edf3;
                        --text-secondary: #8b949e;
                        --blue: #58a6ff;
                        --green: #3fb950;
                        --red: #f85149;
                        --orange: #d29922;
                    }

                    body {
                        background-color: var(--bg);
                        color: var(--text);
                        font-family: 'Noto Sans JP', -apple-system, BlinkMacSystemFont, 'Segoe UI', Helvetica, Arial, sans-serif;
                        font-size: 14px;
                        line-height: 1.6;
                        display: flex;
                        min-height: 100vh;
                    }

                    a {
                        color: var(--blue);
                        text-decoration: none;
                    }
                    a:hover {
                        text-decoration: underline;
                    }

                    /* ── Sidebar ── */
                    .sidebar {
                        width: 220px;
                        min-height: 100vh;
                        background-color: var(--secondary);
                        border-right: 1px solid var(--border);
                        display: flex;
                        flex-direction: column;
                        padding: 24px 0;
                        position: fixed;
                        top: 0;
                        left: 0;
                        bottom: 0;
                    }

                    .sidebar-header {
                        padding: 0 20px 24px;
                        border-bottom: 1px solid var(--border);
                        margin-bottom: 16px;
                    }

                    .sidebar-title {
                        font-size: 20px;
                        font-weight: 700;
                        color: var(--text);
                        line-height: 1.2;
                    }

                    .sidebar-subtitle {
                        font-size: 12px;
                        color: var(--text-secondary);
                        margin-top: 2px;
                    }

                    .nav-list {
                        list-style: none;
                        padding: 0 8px;
                    }

                    .nav-list li {
                        margin-bottom: 2px;
                    }

                    .nav-link {
                        display: flex;
                        align-items: center;
                        gap: 10px;
                        padding: 8px 12px;
                        border-radius: 6px;
                        color: var(--text-secondary);
                        font-size: 14px;
                        font-weight: 500;
                        transition: background-color 0.15s, color 0.15s;
                        text-decoration: none;
                    }

                    .nav-link:hover {
                        background-color: var(--tertiary);
                        color: var(--text);
                        text-decoration: none;
                    }

                    .nav-link.active {
                        background-color: var(--tertiary);
                        color: var(--text);
                    }

                    .nav-link .nav-icon {
                        font-size: 16px;
                        width: 20px;
                        text-align: center;
                    }

                    /* ── Main content ── */
                    .main-content {
                        margin-left: 220px;
                        flex: 1;
                        padding: 32px;
                        max-width: calc(100vw - 220px);
                    }

                    .page-header {
                        margin-bottom: 24px;
                        padding-bottom: 16px;
                        border-bottom: 1px solid var(--border);
                    }

                    .page-title {
                        font-size: 24px;
                        font-weight: 700;
                        color: var(--text);
                    }

                    .page-subtitle {
                        color: var(--text-secondary);
                        margin-top: 4px;
                        font-size: 13px;
                    }

                    /* ── Cards ── */
                    .card {
                        background-color: var(--secondary);
                        border: 1px solid var(--border);
                        border-radius: 8px;
                        padding: 20px;
                        margin-bottom: 16px;
                    }

                    .card-title {
                        font-size: 16px;
                        font-weight: 600;
                        color: var(--text);
                        margin-bottom: 8px;
                    }

                    .card-description {
                        color: var(--text-secondary);
                        font-size: 13px;
                        line-height: 1.6;
                        margin-bottom: 12px;
                    }

                    .card-footer {
                        display: flex;
                        align-items: center;
                        gap: 12px;
                        flex-wrap: wrap;
                    }

                    /* ── Tags ── */
                    .tag {
                        display: inline-block;
                        padding: 2px 8px;
                        border-radius: 12px;
                        font-size: 11px;
                        font-weight: 500;
                        background-color: var(--tertiary);
                        color: var(--text-secondary);
                        border: 1px solid var(--border);
                    }

                    /* ── Buttons ── */
                    .btn {
                        display: inline-flex;
                        align-items: center;
                        gap: 6px;
                        padding: 6px 14px;
                        border-radius: 6px;
                        font-size: 13px;
                        font-weight: 500;
                        cursor: pointer;
                        border: 1px solid var(--border);
                        background-color: var(--tertiary);
                        color: var(--text);
                        text-decoration: none;
                        transition: background-color 0.15s;
                    }

                    .btn:hover {
                        background-color: #30363d;
                        text-decoration: none;
                    }

                    .btn-primary {
                        background-color: #238636;
                        border-color: #2ea043;
                        color: #fff;
                    }

                    .btn-primary:hover {
                        background-color: #2ea043;
                    }

                    /* ── Calendar ── */
                    .calendar-nav {
                        display: flex;
                        align-items: center;
                        gap: 16px;
                        margin-bottom: 20px;
                    }

                    .calendar-nav h2 {
                        font-size: 18px;
                        font-weight: 600;
                    }

                    .calendar-grid {
                        display: grid;
                        grid-template-columns: repeat(7, 1fr);
                        gap: 4px;
                    }

                    .calendar-header-cell {
                        text-align: center;
                        font-size: 12px;
                        font-weight: 600;
                        color: var(--text-secondary);
                        padding: 8px 4px;
                    }

                    .calendar-header-cell.sunday { color: #f85149; }
                    .calendar-header-cell.saturday { color: var(--blue); }

                    .calendar-cell {
                        background-color: var(--secondary);
                        border: 1px solid var(--border);
                        border-radius: 6px;
                        min-height: 72px;
                        padding: 6px 8px;
                        position: relative;
                    }

                    .calendar-cell.empty {
                        background-color: transparent;
                        border-color: transparent;
                    }

                    .calendar-cell.today {
                        border-color: var(--blue);
                    }

                    .calendar-day {
                        font-size: 13px;
                        font-weight: 600;
                        color: var(--text);
                        line-height: 1;
                        margin-bottom: 4px;
                    }

                    .calendar-cell.sunday .calendar-day { color: #f85149; }
                    .calendar-cell.saturday .calendar-day { color: var(--blue); }

                    .calendar-cell.has-diary {
                        background-color: #0d2818;
                        border-color: #2ea043;
                    }

                    .diary-dot {
                        display: inline-block;
                        width: 6px;
                        height: 6px;
                        background-color: var(--green);
                        border-radius: 50%;
                        margin-left: 4px;
                        vertical-align: middle;
                    }

                    .calendar-badges {
                        display: flex;
                        gap: 3px;
                        flex-wrap: wrap;
                        margin-top: 4px;
                    }

                    .badge {
                        font-size: 10px;
                        padding: 1px 5px;
                        border-radius: 3px;
                        font-weight: 500;
                    }

                    .badge-swim {
                        background-color: #1f4e79;
                        color: #9ecbff;
                    }

                    .badge-study {
                        background-color: #1a3a1a;
                        color: #56d364;
                    }

                    .badge-commit {
                        background-color: #3b2f0d;
                        color: #e3b341;
                    }

                    .diary-link {
                        font-size: 11px;
                        color: var(--green);
                        text-decoration: none;
                    }

                    .diary-link:hover {
                        text-decoration: underline;
                    }

                    /* ── Progress bar ── */
                    .progress-bar-container {
                        height: 8px;
                        background-color: var(--tertiary);
                        border-radius: 4px;
                        overflow: hidden;
                        margin: 8px 0;
                    }

                    .progress-bar-fill {
                        height: 100%;
                        background-color: var(--green);
                        border-radius: 4px;
                        transition: width 0.3s;
                    }

                    .progress-text {
                        font-size: 12px;
                        color: var(--text-secondary);
                    }

                    /* ── Activity list ── */
                    .activity-item {
                        display: flex;
                        align-items: flex-start;
                        gap: 12px;
                        padding: 12px 0;
                        border-bottom: 1px solid var(--border);
                    }

                    .activity-item:last-child {
                        border-bottom: none;
                    }

                    .activity-source {
                        font-size: 11px;
                        font-weight: 600;
                        padding: 2px 6px;
                        border-radius: 4px;
                        background-color: var(--tertiary);
                        color: var(--text-secondary);
                        white-space: nowrap;
                        min-width: 70px;
                        text-align: center;
                    }

                    .activity-title {
                        flex: 1;
                        font-size: 13px;
                        color: var(--text);
                    }

                    .activity-date {
                        font-size: 11px;
                        color: var(--text-secondary);
                        white-space: nowrap;
                    }

                    /* ── Records ── */
                    .records-tabs {
                        display: flex;
                        gap: 4px;
                        margin-bottom: 20px;
                        border-bottom: 1px solid var(--border);
                        padding-bottom: 0;
                    }

                    .records-tab {
                        padding: 8px 16px;
                        font-size: 13px;
                        font-weight: 500;
                        color: var(--text-secondary);
                        text-decoration: none;
                        border-bottom: 2px solid transparent;
                        margin-bottom: -1px;
                        transition: color 0.15s;
                    }

                    .records-tab:hover {
                        color: var(--text);
                        text-decoration: none;
                    }

                    .records-tab.active {
                        color: var(--text);
                        border-bottom-color: var(--blue);
                    }

                    .record-item {
                        display: flex;
                        align-items: flex-start;
                        gap: 12px;
                        padding: 12px 0;
                        border-bottom: 1px solid var(--border);
                    }

                    .record-item:last-child { border-bottom: none; }

                    .record-date {
                        font-size: 11px;
                        color: var(--text-secondary);
                        white-space: nowrap;
                        min-width: 80px;
                    }

                    .record-title {
                        flex: 1;
                        font-size: 13px;
                        color: var(--text);
                    }

                    .record-note {
                        font-size: 12px;
                        color: var(--text-secondary);
                        margin-top: 3px;
                    }

                    /* ── Admin / Login ── */
                    .login-container {
                        max-width: 400px;
                        margin: 0 auto;
                        padding-top: 60px;
                    }

                    .form-group {
                        margin-bottom: 16px;
                    }

                    .form-label {
                        display: block;
                        font-size: 13px;
                        font-weight: 500;
                        color: var(--text);
                        margin-bottom: 6px;
                    }

                    .form-input {
                        width: 100%;
                        padding: 8px 12px;
                        background-color: var(--tertiary);
                        border: 1px solid var(--border);
                        border-radius: 6px;
                        color: var(--text);
                        font-size: 14px;
                        font-family: inherit;
                        outline: none;
                        transition: border-color 0.15s;
                    }

                    .form-input:focus {
                        border-color: var(--blue);
                    }

                    .error-message {
                        padding: 10px 14px;
                        background-color: #2d1117;
                        border: 1px solid var(--red);
                        border-radius: 6px;
                        color: var(--red);
                        font-size: 13px;
                        margin-bottom: 16px;
                    }

                    .diary-table {
                        width: 100%;
                        border-collapse: collapse;
                    }

                    .diary-table th, .diary-table td {
                        padding: 10px 12px;
                        text-align: left;
                        border-bottom: 1px solid var(--border);
                        font-size: 13px;
                    }

                    .diary-table th {
                        font-weight: 600;
                        color: var(--text-secondary);
                        background-color: var(--tertiary);
                    }

                    .diary-table tr:hover td {
                        background-color: var(--tertiary);
                    }

                    /* ── Responsive ── */
                    @media (max-width: 768px) {
                        .sidebar {
                            width: 60px;
                            padding: 16px 0;
                        }
                        .sidebar-header { padding: 0 8px 16px; }
                        .sidebar-title, .sidebar-subtitle { display: none; }
                        .nav-link span:not(.nav-icon) { display: none; }
                        .nav-link { padding: 10px; justify-content: center; }
                        .main-content { margin-left: 60px; padding: 20px 16px; }
                    }
                """.trimIndent())
            }
        }
    }
    body {
        div(classes = "sidebar") {
            div(classes = "sidebar-header") {
                div(classes = "sidebar-title") { +"Suisan" }
                div(classes = "sidebar-subtitle") { +"Portfolio" }
            }
            ul(classes = "nav-list") {
                data class NavItem(val href: String, val icon: String, val label: String, val key: String)
                val navItems = listOf(
                    NavItem("/", "📅", "Calendar", "calendar"),
                    NavItem("/projects", "🚀", "Projects", "projects"),
                    NavItem("/goals", "🎯", "Goals", "goals"),
                    NavItem("/activity", "⚡", "Activity", "activity"),
                    NavItem("/records", "📖", "Records", "records"),
                    NavItem("/admin", "⚙️", "Admin", "admin")
                )
                for (item in navItems) {
                    li {
                        a(href = item.href, classes = if (currentPage == item.key) "nav-link active" else "nav-link") {
                            span(classes = "nav-icon") { +item.icon }
                            span { +item.label }
                        }
                    }
                }
            }
        }
        div(classes = "main-content") {
            content(this)
        }
    }
}
