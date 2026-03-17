package com.lifelog.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        // Neon などの DATABASE_URL は "postgresql://..." 形式で来るので jdbc: プレフィックスを補完
        val rawUrl = System.getenv("DATABASE_URL")
            ?: "jdbc:postgresql://localhost:5432/lifelog"
        val jdbcUrl = when {
            rawUrl.startsWith("postgres://")   -> rawUrl.replace("postgres://", "jdbc:postgresql://")
            rawUrl.startsWith("postgresql://") -> rawUrl.replace("postgresql://", "jdbc:postgresql://")
            else -> rawUrl
        }

        Database.connect(
            url = jdbcUrl,
            driver = "org.postgresql.Driver"
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
