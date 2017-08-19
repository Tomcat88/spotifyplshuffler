package it.introini.spotifyplshuffler.routes

import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import it.introini.spotifyplshuffler.manager.token.Token
import it.introini.spotifyplshuffler.manager.token.TokenManager
import it.introini.spotifyplshuffler.spotify.SpotifyClient
import org.pmw.tinylog.Logger
import java.time.Instant
import java.util.*

abstract class AbstractHandler constructor(protected val tokenManager: TokenManager,
                                           protected val spotifyClient: SpotifyClient) : Handler<RoutingContext> {

    fun checkAuth(event: RoutingContext): Token? {
        val auth = event.request().getHeader(HttpHeaderNames.AUTHORIZATION)?.removePrefix("Basic ")
        val token = auth?.let {
            String(Base64.getDecoder().decode(it)).split(":").component1()
        }?.let {
            tokenManager.getToken(it)
        }
        if (token == null) {
            event.response().statusCode = HttpResponseStatus.UNAUTHORIZED.code()
            event.response().end()
            return null
        } else if (token.isExpired(Instant.now())) {
            Logger.info("Token fo user ${token.userId} is expired, refreshing...")
            tokenManager.deleteToken(token.userId)
            val (error, data) = spotifyClient.refreshToken(token)
            if (error != null) {
                Logger.error("... could not refresh token for user ${token.userId}")
                event.response().statusCode = HttpResponseStatus.UNAUTHORIZED.code()
                event.response().end()
                return null
            } else {
                return tokenManager.insertToken(data!!.getString("access_token"),
                                                data.getString("token_type"),
                                                data.getString("scope"),
                                                data.getLong("expires_in"),
                                                data.getString("refresh_token"),
                                                Instant.now(),
                                                token.userId)
            }
        } else {
            return token
        }
    }
}