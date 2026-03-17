package com.lifelog.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val rawUrl = System.getenv("DATABASE_URL")
            ?: "postgresql://localhost:5432/lifelog"

        // URI パースで user/password を分離して JDBC に渡す
        val normalized = rawUrl
            .replace("postgres://", "http://")
            .replace("postgresql://", "http://")
            .replace(Regex("[?&]channel_binding=[^&]*"), "")

        val uri = java.net.URI(normalized)
        val userInfo = uri.userInfo?.split(":", limit = 2) ?: listOf("", "")
        val user = userInfo[0]
        val password = userInfo.getOrElse(1) { "" }

        // クエリパラメータから channel_binding を除いて sslmode だけ残す
        val query = uri.query
            ?.split("&")
            ?.filter { !it.startsWith("channel_binding") }
            ?.joinToString("&")
            ?.let { if (it.isNotEmpty()) "?$it" else "" }
            ?: ""

        val port = if (uri.port > 0) ":${uri.port}" else ""
        val jdbcUrl = "jdbc:postgresql://${uri.host}$port${uri.path}$query"

        Database.connect(
            url = jdbcUrl,
            driver = "org.postgresql.Driver",
            user = user,
            password = password
        )

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                DailyLogs,
                Diaries,
                Projects,
                Goals,
                ExternalActivities,
                Records
            )
        }
    }
}
