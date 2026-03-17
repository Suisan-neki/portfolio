package com.lifelog.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object DatabaseFactory {
    fun init() {
        val config = parseDatabaseConfig(System.getenv("DATABASE_URL"))

        Database.connect(
            url = config.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = config.user,
            password = config.password
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

    internal fun parseDatabaseConfig(rawUrl: String?): DatabaseConfig {
        val value = rawUrl
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: "jdbc:postgresql://localhost:5432/lifelog"

        val jdbcCandidate = when {
            value.startsWith("jdbc:postgresql://") -> value.removePrefix("jdbc:")
            value.startsWith("jdbc:postgres://") -> value.removePrefix("jdbc:")
                .replaceFirst("postgres://", "postgresql://")
            value.startsWith("postgres://") -> value.replaceFirst("postgres://", "postgresql://")
            value.startsWith("postgresql://") -> value
            else -> return DatabaseConfig(jdbcUrl = value, user = "", password = "")
        }

        val uri = URI(jdbcCandidate.replaceFirst("postgresql://", "http://"))
        val queryParams = parseQueryParams(uri.rawQuery)
        val userInfo = uri.rawUserInfo
            ?.split(":", limit = 2)
            ?.map(::decodeUrlPart)
            ?: emptyList()

        val user = userInfo.firstOrNull()
            ?: queryParams["user"]
            ?: ""
        val password = userInfo.getOrNull(1)
            ?: queryParams["password"]
            ?: ""
        val host = uri.host
            ?: throw IllegalArgumentException("DATABASE_URL host is missing: $value")
        val path = uri.rawPath?.takeIf { it.isNotEmpty() } ?: "/"

        val filteredQuery = queryParams
            .filterKeys { it != "channel_binding" && it != "user" && it != "password" }
            .entries
            .joinToString("&") { (key, parameterValue) ->
                if (parameterValue.isEmpty()) key else "$key=$parameterValue"
            }
            .let { if (it.isEmpty()) "" else "?$it" }

        val port = if (uri.port > 0) ":${uri.port}" else ""

        return DatabaseConfig(
            jdbcUrl = "jdbc:postgresql://$host$port$path$filteredQuery",
            user = user,
            password = password
        )
    }

    private fun parseQueryParams(rawQuery: String?): LinkedHashMap<String, String> {
        val params = linkedMapOf<String, String>()
        if (rawQuery.isNullOrEmpty()) return params

        rawQuery.split("&")
            .filter { it.isNotEmpty() }
            .forEach { segment ->
                val parts = segment.split("=", limit = 2)
                val key = decodeUrlPart(parts[0])
                val value = parts.getOrElse(1) { "" }.let(::decodeUrlPart)
                params[key] = value
            }

        return params
    }

    private fun decodeUrlPart(value: String): String =
        URLDecoder.decode(value, StandardCharsets.UTF_8)
}

internal data class DatabaseConfig(
    val jdbcUrl: String,
    val user: String,
    val password: String
)
