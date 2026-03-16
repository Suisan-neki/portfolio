package com.lifelog.services

import com.lifelog.database.Goals
import com.lifelog.models.CreateGoalRequest
import com.lifelog.models.Goal
import com.lifelog.models.UpdateGoalProgressRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object GoalService {

    private val dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun getAllGoals(): List<Goal> = transaction {
        Goals.selectAll()
            .orderBy(Goals.createdAt, SortOrder.DESC)
            .map { row -> row.toGoal() }
    }

    fun createGoal(req: CreateGoalRequest): Goal = transaction {
        val now = LocalDateTime.now().format(dtf)
        val id = Goals.insertAndGetId {
            it[title] = req.title
            it[description] = req.description
            it[target] = req.target
            it[progress] = 0
            it[createdAt] = now
        }.value

        Goal(
            id = id,
            title = req.title,
            description = req.description,
            target = req.target,
            progress = 0,
            createdAt = now
        )
    }

    fun updateProgress(id: Int, req: UpdateGoalProgressRequest): Goal = transaction {
        val updated = Goals.update({ Goals.id eq id }) {
            it[progress] = req.progress
        }
        if (updated == 0) throw NoSuchElementException("Goal not found: $id")
        Goals.selectAll()
            .where { Goals.id eq id }
            .map { row -> row.toGoal() }
            .first()
    }

    fun deleteGoal(id: Int) = transaction {
        Goals.deleteWhere { Goals.id eq id }
    }

    private fun ResultRow.toGoal() = Goal(
        id = this[Goals.id].value,
        title = this[Goals.title],
        description = this[Goals.description],
        target = this[Goals.target],
        progress = this[Goals.progress],
        createdAt = this[Goals.createdAt]
    )
}
