package com.lifelog

import com.lifelog.database.DatabaseFactory
import com.lifelog.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureCORS()
    configureAuth()
    configureStatusPages()
    configureRouting()
}
