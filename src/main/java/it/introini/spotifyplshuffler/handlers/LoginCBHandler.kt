package it.introini.spotifyplshuffler.handlers

import com.google.inject.Inject
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import it.introini.spotifyplshuffler.manager.token.Token
import it.introini.spotifyplshuffler.manager.state.StateManager
import it.introini.spotifyplshuffler.manager.token.TokenManager
import it.introini.spotifyplshuffler.spotify.SpotifyClient
import it.introini.spotifyplshuffler.spotify.SpotifyUser
import org.pmw.tinylog.Logger
import java.time.Instant
import java.util.*

class LoginCBHandler @Inject constructor(val stateManager: StateManager,
                                         val spotifyClient: SpotifyClient,
                                         val tokenManager: TokenManager) : Handler<RoutingContext>{
    override fun handle(event: RoutingContext) {
        val cookie = event.getCookie("state")
        val request = event.request()
        val response = event.response()

        if (cookie != null && stateManager.containsState(cookie.value)) {
            stateManager.removeState(cookie.value)
            val error = request.getParam("error")
            if (error == null) {
                val code = request.getParam("code")
                val tokensFuture = Future.future<JsonObject>()
                spotifyClient.requestTokens(code, tokensFuture)
                tokensFuture.setHandler {
                    if (it.succeeded()) {
                        Logger.info("Successfully retrieved tokens!")
                        val jsonToken = it.result()
                        Logger.info(jsonToken.encodePrettily())
                        val userId = UUID.randomUUID().toString()
                        val token = tokenManager.insertToken(jsonToken.getString("access_token"),
                                                             jsonToken.getString("token_type"),
                                                             jsonToken.getString("scope"),
                                                             jsonToken.getLong("expires_in"),
                                                             jsonToken.getString("refresh_token"),
                                                             Instant.now(),
                                                             userId)
                        val meFuture = Future.future<SpotifyUser>()
                        spotifyClient.getMe(token, meFuture)
                        meFuture.setHandler {
                            if (it.succeeded()) {
                                val spotifyUser = it.result()
                                Logger.info(spotifyUser)
                                response.end(JsonObject().put("userId", userId)
                                                         .put("spotify_user", JsonObject.mapFrom(spotifyUser)).encode())
                            } else {
                                event.fail(it.cause())
                            }
                        }
                    } else {
                        Logger.error(it.cause(), "Error while retrieving tokens")
                        event.fail(HttpResponseStatus.UNAUTHORIZED.code())
                    }
                }
            } else {
                Logger.error("Error authorizing app ($error)")
                event.fail(HttpResponseStatus.UNAUTHORIZED.code())
            }
        } else {
            event.fail(HttpResponseStatus.UNAUTHORIZED.code())
        }
    }
}