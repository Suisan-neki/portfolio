package com.lifelog.services

import com.lifelog.database.Records
import com.lifelog.models.CreateRecordRequest
import com.lifelog.models.Record
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object RecordService {

    private val dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun getAllRecords(category: String? = null): List<Record> = transaction {
        val query = if (category != null) {
            Records.selectAll().where { Records.category eq category }
        } else {
            Records.selectAll()
        }
        query
            .orderBy(Records.date, SortOrder.DESC)
            .map { row -> row.toRecord() }
    }

    fun createRecord(req: CreateRecordRequest): Record = transaction {
        val now = LocalDateTime.now().format(dtf)
        val id = Records.insertAndGetId {
            it[category] = req.category
            it[title] = req.title
            it[url] = req.url
            it[date] = req.date
            it[note] = req.note
            it[createdAt] = now
        }.value

        Record(
            id = id,
            category = req.category,
            title = req.title,
            url = req.url,
            date = req.date,
            note = req.note,
            createdAt = now
        )
    }

    fun updateRecord(id: Int, req: CreateRecordRequest): Record = transaction {
        val updated = Records.update({ Records.id eq id }) {
            it[category] = req.category
            it[title] = req.title
            it[url] = req.url
            it[date] = req.date
            it[note] = req.note
        }
        if (updated == 0) throw NoSuchElementException("Record not found: $id")
        Records.selectAll()
            .where { Records.id eq id }
            .map { row -> row.toRecord() }
            .first()
    }

    fun deleteRecord(id: Int) = transaction {
        Records.deleteWhere { Records.id eq id }
    }

    private fun ResultRow.toRecord() = Record(
        id = this[Records.id].value,
        category = this[Records.category],
        title = this[Records.title],
        url = this[Records.url],
        date = this[Records.date],
        note = this[Records.note],
        createdAt = this[Records.createdAt]
    )
}
