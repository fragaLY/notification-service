package by.integrated.notification.handler

import by.integrated.notification.domain.Notification
import by.integrated.notification.repository.Repository
import by.integrated.notification.router.Router.Companion.PATH
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import java.net.URI

@Service
class Handler(private val repository: Repository) {

    companion object {
        const val USER_PATH_VARIABLE = "userId"
        const val NOTIFICATION_PATH_VARIABLE = "id"
    }

    suspend fun all(request: ServerRequest) = ServerResponse.ok().bodyAndAwait(repository.all())

    suspend fun allForUser(request: ServerRequest) =
        ServerResponse.ok().bodyAndAwait(repository.forUser(request.pathVariable(USER_PATH_VARIABLE).toLong()))

    suspend fun oneForUser(request: ServerRequest) =
        repository.forUser(
            request.pathVariable(NOTIFICATION_PATH_VARIABLE).toLong(),
            request.pathVariable(USER_PATH_VARIABLE).toLong()
        )?.let {
            ServerResponse.ok().bodyValueAndAwait(it)
        } ?: ServerResponse.notFound().buildAndAwait()

    suspend fun send(request: ServerRequest) =
        repository.create(Notification(null, request.pathVariable(USER_PATH_VARIABLE).toLong()))?.let {
            //send notification logic could be there
            ServerResponse.created(URI.create("${PATH}/$it")).buildAndAwait()
        } ?: ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).buildAndAwait()

    suspend fun deleteOneForUser(request: ServerRequest) =
        repository.forUser(
            request.pathVariable(NOTIFICATION_PATH_VARIABLE).toLong(),
            request.pathVariable(USER_PATH_VARIABLE).toLong()
        )?.let {
            repository.delete(request.pathVariable(NOTIFICATION_PATH_VARIABLE).toLong())
                .let { ServerResponse.noContent().buildAndAwait() }
        } ?: ServerResponse.notFound().buildAndAwait()

    suspend fun deleteAllForUser(request: ServerRequest) =
        repository.deleteAllForUser(request.pathVariable(USER_PATH_VARIABLE).toLong()).run {
            ServerResponse.noContent().buildAndAwait()
        }
}

