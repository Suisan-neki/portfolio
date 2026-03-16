package com.lifelog.services

import com.lifelog.database.ExternalActivities
import com.lifelog.models.ExternalActivity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object ActivityService {

    fun getAllActivities(): List<ExternalActivity> = transaction {
        ExternalActivities.selectAll()
            .orderBy(ExternalActivities.createdAt, SortOrder.DESC)
            .map { row ->
                ExternalActivity(
                    id = row[ExternalActivities.id].value,
                    source = row[ExternalActivities.activitySource],
                    title = row[ExternalActivities.title],
                    url = row[ExternalActivities.url],
                    createdAt = row[ExternalActivities.createdAt]
                )
            }
    }
}
