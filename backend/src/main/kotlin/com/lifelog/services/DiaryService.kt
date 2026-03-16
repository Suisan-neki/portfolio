package com.lifelog.services

import com.lifelog.database.DailyLogs
import com.lifelog.database.Diaries
import com.lifelog.models.CreateDiaryRequest
import com.lifelog.models.DailyLog
import com.lifelog.models.Diary
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DiaryService {

    private val dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun getMonthlyLogs(year: Int, month: Int): List<DailyLog> = transaction {
        val start = "%04d-%02d-01".format(year, month)
        val end = "%04d-%02d-31".format(year, month)
        DailyLogs.selectAll()
            .where { DailyLogs.date.between(start, end) }
            .map { row ->
                DailyLog(
                    date = row[DailyLogs.date],
                    swim = row[DailyLogs.swim],
                    study = row[DailyLogs.study],
                    commitCount = row[DailyLogs.commitCount],
                    diaryId = row[DailyLogs.diaryId]
                )
            }
    }

    fun getDiary(date: String): Diary? = transaction {
        Diaries.selectAll()
            .where { Diaries.date eq date }
            .map { row -> row.toDiary() }
            .firstOrNull()
    }

    fun getAllDiaries(): List<Diary> = transaction {
        Diaries.selectAll()
            .orderBy(Diaries.date, SortOrder.DESC)
            .map { row -> row.toDiary() }
    }

    fun createDiary(req: CreateDiaryRequest): Diary = transaction {
        val now = LocalDateTime.now().format(dtf)
        val id = Diaries.insertAndGetId {
            it[date] = req.date
            it[title] = req.title
            it[contentMarkdown] = req.contentMarkdown
            it[createdAt] = now
        }.value

        // Update or create daily_log entry
        val existing = DailyLogs.selectAll()
            .where { DailyLogs.date eq req.date }
            .firstOrNull()

        if (existing != null) {
            DailyLogs.update({ DailyLogs.date eq req.date }) {
                it[diaryId] = id
            }
        } else {
            DailyLogs.insert {
                it[date] = req.date
                it[swim] = false
                it[study] = false
                it[commitCount] = 0
                it[diaryId] = id
            }
        }

        Diary(
            id = id,
            date = req.date,
            title = req.title,
            contentMarkdown = req.contentMarkdown,
            createdAt = now
        )
    }

    fun updateDiary(id: Int, req: CreateDiaryRequest): Diary = transaction {
        val updated = Diaries.update({ Diaries.id eq id }) {
            it[title] = req.title
            it[contentMarkdown] = req.contentMarkdown
        }
        if (updated == 0) throw NoSuchElementException("Diary not found: $id")
        Diaries.selectAll()
            .where { Diaries.id eq id }
            .map { row -> row.toDiary() }
            .first()
    }

    fun deleteDiary(id: Int) = transaction {
        DailyLogs.update({ DailyLogs.diaryId eq id }) {
            it[diaryId] = null
        }
        Diaries.deleteWhere { Diaries.id eq id }
    }

    private fun ResultRow.toDiary() = Diary(
        id = this[Diaries.id].value,
        date = this[Diaries.date],
        title = this[Diaries.title],
        contentMarkdown = this[Diaries.contentMarkdown],
        createdAt = this[Diaries.createdAt]
    )
}
