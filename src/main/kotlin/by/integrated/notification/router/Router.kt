package by.integrated.notification.router

import by.integrated.notification.handler.Handler
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.coRouter

@Component
class Router(private val handler: Handler) {

    companion object {
        const val PATH = "/api/notifications"
        const val USERS_PATH = "/users/{userId}"
        const val BLANK = ""
        const val ID_PATTERN = "/{id}"
    }

    @Bean
    fun coRouter() = coRouter {

        PATH.nest {
            GET(BLANK).and(accept(MediaType.APPLICATION_JSON)).invoke(handler::all)
            GET(USERS_PATH).and(accept(MediaType.APPLICATION_JSON)).invoke(handler::allForUser)
            GET(ID_PATTERN.plus(USERS_PATH)).and(accept(MediaType.APPLICATION_JSON)).invoke(handler::oneForUser)
            POST(USERS_PATH, handler::send)
            DELETE(ID_PATTERN.plus(USERS_PATH), handler::deleteOneForUser)
            DELETE(USERS_PATH, handler::deleteAllForUser)
        }
    }
}