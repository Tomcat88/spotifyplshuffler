package it.introini.spotifyplshuffler.routes

import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import it.introini.spotifyplshuffler.manager.token.Token
import it.introini.spotifyplshuffler.manager.token.TokenManager
import java.util.*

abstract class AbstractHandler constructor(protected val tokenManager: TokenManager) : Handler<RoutingContext> {

    fun checkAuth(event: RoutingContext): Token? {
        val auth = event.request().getHeader(HttpHeaderNames.AUTHORIZATION)?.removePrefix("Basic ")
        val token = auth?.let {
            String(Base64.getDecoder().decode(it)).split(":").component2()
        }?.let {
            tokenManager.getToken(it)
        }
        val tokenAvailable = token == null
        if (tokenAvailable) {
            event.response().statusCode = HttpResponseStatus.UNAUTHORIZED.code()
            event.response().end()
        }
        return token
    }
}