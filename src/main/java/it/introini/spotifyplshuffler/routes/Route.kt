package it.introini.spotifyplshuffler.routes

import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpMethod.GET
import io.vertx.ext.web.RoutingContext
import it.introini.spotifyplshuffler.handlers.LoginCBHandler
import it.introini.spotifyplshuffler.handlers.LoginHandler
import it.introini.spotifyplshuffler.handlers.PlaylistHandler
import it.introini.spotifyplshuffler.handlers.PlaylistTracksHandler


enum class Route(val method: HttpMethod, val endpoint: String, val handler: Class<out Handler<RoutingContext>>) {
    LOGIN          (GET, "/login",   LoginHandler::class.java),
    LOGIN_CALLBACK (GET, "/logincb", LoginCBHandler::class.java),

    PLAYLISTS       (GET, "/playlists", PlaylistHandler::class.java),
    PLAYLIST_TRACKS (GET, "/playlist/:uid/:pl/tracks", PlaylistTracksHandler::class.java)
}