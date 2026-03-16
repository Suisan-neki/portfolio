package com.lifelog.services

import com.lifelog.database.DailyLogs
import com.lifelog.models.DailyLog
import com.lifelog.models.UpdateRoutineRequest
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object RoutineService {

    fun updateRoutine(req: UpdateRoutineRequest): DailyLog = transaction {
        val existing = DailyLogs.selectAll()
            .where { DailyLogs.date eq req.date }
            .firstOrNull()

        if (existing != null) {
            DailyLogs.update({ DailyLogs.date eq req.date }) {
                it[swim] = req.swim
                it[study] = req.study
            }
        } else {
            DailyLogs.insert {
                it[date] = req.date
                it[swim] = req.swim
                it[study] = req.study
                it[commitCount] = 0
                it[diaryId] = null
            }
        }

        DailyLogs.selectAll()
            .where { DailyLogs.date eq req.date }
            .map { row ->
                DailyLog(
                    date = row[DailyLogs.date],
                    swim = row[DailyLogs.swim],
                    study = row[DailyLogs.study],
                    commitCount = row[DailyLogs.commitCount],
                    diaryId = row[DailyLogs.diaryId]
                )
            }
            .first()
    }
}
