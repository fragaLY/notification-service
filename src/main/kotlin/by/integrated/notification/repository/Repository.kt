package by.integrated.notification.repository

import by.integrated.notification.domain.Notification
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.asType
import org.springframework.data.r2dbc.core.awaitOneOrNull
import org.springframework.data.r2dbc.core.flow
import org.springframework.data.r2dbc.core.into
import org.springframework.data.r2dbc.query.Criteria
import org.springframework.stereotype.Repository

@Repository
class Repository(private val client: DatabaseClient) {

    companion object {
        const val TABLE = "NOTIFICATION"
        const val ID = "ID"
        const val USER_ID = "USER_ID"
    }

    suspend fun all() =
        client
            .select()
            .from(TABLE)
            .asType<Notification>()
            .fetch()
            .flow()

    suspend fun forUser(userId: Long) =
        client
            .select()
            .from(TABLE)
            .matching(Criteria.where(USER_ID).`is`(userId))
            .asType<Notification>()
            .fetch()
            .flow()

    suspend fun forUser(notificationId: Long, userId: Long) =
        client
            .select()
            .from(TABLE)
            .matching(Criteria.where(ID).`is`(notificationId).and(USER_ID).`is`(userId))
            .asType<Notification>()
            .fetch()
            .awaitOneOrNull()

    suspend fun create(notification: Notification) =
        client.insert()
            .into<Notification>()
            .table(TABLE)
            .using(notification)
            .map { row, _ -> row.get(ID, Long::class.javaObjectType)?.toLong() }
            .awaitOneOrNull()

    suspend fun delete(notificationId: Long): Int =
        client
            .delete()
            .from(TABLE)
            .matching(Criteria.where(ID).`is`(notificationId))
            .fetch()
            .rowsUpdated()
            .awaitSingle()

    suspend fun deleteAllForUser(userId: Long): Int =
        client
            .delete()
            .from(TABLE)
            .matching(Criteria.where(USER_ID).`is`(userId))
            .fetch()
            .rowsUpdated()
            .awaitLast()
}