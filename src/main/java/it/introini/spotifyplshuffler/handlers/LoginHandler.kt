package it.introini.spotifyplshuffler.handlers

import com.google.inject.Inject
import io.vertx.core.Handler
import io.vertx.ext.web.Cookie
import io.vertx.ext.web.RoutingContext
import it.introini.spotifyplshuffler.manager.state.StateManager
import it.introini.spotifyplshuffler.spotify.SpotifyClient
import java.util.*


class LoginHandler @Inject constructor(val spotifyClient: SpotifyClient, val stateManager: StateManager) : Handler<RoutingContext> {

    override fun handle(event: RoutingContext) {
        val state = UUID.randomUUID().toString().slice(IntRange(0, 15))
        stateManager.addState(state)
        event.addCookie(Cookie.cookie("state", state))
        event.response()
                .putHeader("location", spotifyClient.getAuthorizeUrl(state))
                .setStatusCode(302).end()

    }

}