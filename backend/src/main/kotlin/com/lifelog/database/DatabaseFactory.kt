package com.lifelog.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val dbPath = System.getenv("DB_PATH") ?: "./lifelog.db"
        Database.connect(
            url = "jdbc:sqlite:$dbPath",
            driver = "org.sqlite.JDBC"
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
