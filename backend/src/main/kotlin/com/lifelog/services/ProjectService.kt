package com.lifelog.services

import com.lifelog.database.Projects
import com.lifelog.models.CreateProjectRequest
import com.lifelog.models.Project
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ProjectService {

    private val dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val json = Json { ignoreUnknownKeys = true }

    fun getAllProjects(): List<Project> = transaction {
        Projects.selectAll()
            .orderBy(Projects.createdAt, SortOrder.DESC)
            .map { row -> row.toProject() }
    }

    fun createProject(req: CreateProjectRequest): Project = transaction {
        val now = LocalDateTime.now().format(dtf)
        val id = Projects.insertAndGetId {
            it[title] = req.title
            it[description] = req.description
            it[tags] = json.encodeToString(req.tags)
            it[githubUrl] = req.githubUrl
            it[demoUrl] = req.demoUrl
            it[createdAt] = now
        }.value

        Project(
            id = id,
            title = req.title,
            description = req.description,
            tags = req.tags,
            githubUrl = req.githubUrl,
            demoUrl = req.demoUrl,
            createdAt = now
        )
    }

    fun updateProject(id: Int, req: CreateProjectRequest): Project = transaction {
        val updated = Projects.update({ Projects.id eq id }) {
            it[title] = req.title
            it[description] = req.description
            it[tags] = json.encodeToString(req.tags)
            it[githubUrl] = req.githubUrl
            it[demoUrl] = req.demoUrl
        }
        if (updated == 0) throw NoSuchElementException("Project not found: $id")
        Projects.selectAll()
            .where { Projects.id eq id }
            .map { row -> row.toProject() }
            .first()
    }

    fun deleteProject(id: Int) = transaction {
        Projects.deleteWhere { Projects.id eq id }
    }

    private fun ResultRow.toProject(): Project {
        val tagsStr = this[Projects.tags]
        val tagsList = try {
            json.decodeFromString<List<String>>(tagsStr)
        } catch (_: Exception) {
            emptyList()
        }
        return Project(
            id = this[Projects.id].value,
            title = this[Projects.title],
            description = this[Projects.description],
            tags = tagsList,
            githubUrl = this[Projects.githubUrl],
            demoUrl = this[Projects.demoUrl],
            createdAt = this[Projects.createdAt]
        )
    }
}
