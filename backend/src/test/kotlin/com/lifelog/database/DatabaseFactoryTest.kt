package com.lifelog.database

import kotlin.test.Test
import kotlin.test.assertEquals

class DatabaseFactoryTest {
    @Test
    fun `parses postgres url with embedded credentials`() {
        val config = DatabaseFactory.parseDatabaseConfig(
            "postgresql://neondb_owner:npg_secret@ep-dark-river-a1600abw-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require"
        )

        assertEquals(
            "jdbc:postgresql://ep-dark-river-a1600abw-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require",
            config.jdbcUrl
        )
        assertEquals("neondb_owner", config.user)
        assertEquals("npg_secret", config.password)
    }

    @Test
    fun `parses jdbc postgres url with embedded credentials`() {
        val config = DatabaseFactory.parseDatabaseConfig(
            "jdbc:postgresql://neondb_owner:npg_secret@ep-dark-river-a1600abw-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require"
        )

        assertEquals(
            "jdbc:postgresql://ep-dark-river-a1600abw-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require",
            config.jdbcUrl
        )
        assertEquals("neondb_owner", config.user)
        assertEquals("npg_secret", config.password)
    }

    @Test
    fun `keeps jdbc url and extracts query credentials`() {
        val config = DatabaseFactory.parseDatabaseConfig(
            "jdbc:postgresql://db.example.com:5432/lifelog?user=app_user&password=app_pass&sslmode=require"
        )

        assertEquals(
            "jdbc:postgresql://db.example.com:5432/lifelog?sslmode=require",
            config.jdbcUrl
        )
        assertEquals("app_user", config.user)
        assertEquals("app_pass", config.password)
    }

    @Test
    fun `falls back to local jdbc url when env is blank`() {
        val config = DatabaseFactory.parseDatabaseConfig("   ")

        assertEquals("jdbc:postgresql://localhost:5432/lifelog", config.jdbcUrl)
        assertEquals("", config.user)
        assertEquals("", config.password)
    }
}
