plugins {
    kotlin("multiplatform") version "2.1.0"
    id("org.jetbrains.compose") version "1.7.3"
    kotlin("plugin.compose") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
}

// ビルド時に API_BASE_URL 環境変数を定数として埋め込む
// ローカル開発: 空文字 → 相対パス /api (Vite proxy の代わりにバックエンドを直接起動)
// 本番 (GitHub Actions): API_BASE_URL=https://your-backend.up.railway.app
val apiBaseUrl = System.getenv("API_BASE_URL") ?: ""

val generateApiConfig by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/api")
    outputs.dir(outputDir)
    doLast {
        val file = outputDir.get().file("ApiConfig.kt").asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            package com.lifelog.web.api

            /** ビルド時に注入されたバックエンドのベースURL */
            const val API_BASE_URL = "$apiBaseUrl"
            """.trimIndent()
        )
    }
}

kotlin {
    wasmJs {
        moduleName = "portfolio"
        browser {
            commonWebpackConfig {
                outputFileName = "portfolio.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(layout.buildDirectory.dir("generated"))

            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

                implementation("io.ktor:ktor-client-core:3.0.3")
                implementation("io.ktor:ktor-client-content-negotiation:3.0.3")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.3")
            }
        }

        val wasmJsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:3.0.3")
            }
        }
    }
}

tasks.named("compileKotlinWasmJs") {
    dependsOn(generateApiConfig)
}
