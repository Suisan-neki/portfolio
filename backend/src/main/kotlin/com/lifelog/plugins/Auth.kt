package com.lifelog.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureAuth() {
    install(Authentication) {
        bearer("admin-auth") {
            authenticate { tokenCredential ->
                val expectedToken = System.getenv("ADMIN_TOKEN") ?: "lifelog-admin-token"
                if (tokenCredential.token == expectedToken) {
                    UserIdPrincipal("admin")
                } else {
                    null
                }
            }
        }
    }
}
