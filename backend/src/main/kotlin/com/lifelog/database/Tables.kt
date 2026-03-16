package com.lifelog.database

import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * daily_log: 1日1レコードのコアテーブル
 * PK は date (VARCHAR) なので通常の Table を使う
 */
object DailyLogs : org.jetbrains.exposed.sql.Table("daily_log") {
    val date = varchar("date", 10)           // YYYY-MM-DD (PK)
    val swim = bool("swim").default(false)
    val study = bool("study").default(false)
    val commitCount = integer("commit_count").default(0)
    val diaryId = integer("diary_id").nullable()

    override val primaryKey = PrimaryKey(date)
}

/**
 * diary: 日記エントリ
 */
object Diaries : IntIdTable("diary") {
    val date = varchar("date", 10)
    val title = varchar("title", 255)
    val contentMarkdown = text("content_markdown")
    val createdAt = varchar("created_at", 30)
}

/**
 * projects: ポートフォリオプロジェクト
 */
object Projects : IntIdTable("projects") {
    val title = varchar("title", 255)
    val description = text("description")
    val tags = text("tags")                  // JSON array as string
    val githubUrl = varchar("github_url", 500).nullable()
    val demoUrl = varchar("demo_url", 500).nullable()
    val createdAt = varchar("created_at", 30)
}

/**
 * goals: 目標管理
 */
object Goals : IntIdTable("goals") {
    val title = varchar("title", 255)
    val description = text("description").default("")
    val target = integer("target").default(10)
    val progress = integer("progress").default(0)
    val createdAt = varchar("created_at", 30)
}

/**
 * external_activity: 外部活動（GitHub/Qiita/RSS）
 */
object ExternalActivities : IntIdTable("external_activity") {
    val activitySource = varchar("source", 20)   // github | qiita | rss
    val title = varchar("title", 500)
    val url = varchar("url", 1000)
    val createdAt = varchar("created_at", 30)
}

/**
 * records: 参加イベント・カンファレンス・読んだ技術書の時系列記録
 * category: event | conference | book
 */
object Records : IntIdTable("records") {
    val category = varchar("category", 20)   // event | conference | book
    val title = varchar("title", 500)
    val url = varchar("url", 1000).nullable()
    val date = varchar("date", 10)           // YYYY-MM-DD (参加日 or 読了日)
    val note = text("note").default("")      // 一言メモ
    val createdAt = varchar("created_at", 30)
}
