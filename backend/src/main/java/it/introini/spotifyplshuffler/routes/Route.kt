package it.introini.spotifyplshuffler.routes

import io.vertx.core.Handler
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpMethod.GET
import io.vertx.core.http.HttpMethod.POST
import io.vertx.ext.web.RoutingContext
import it.introini.spotifyplshuffler.handlers.*


enum class Route(val method: HttpMethod, val endpoint: String, val handler: Class<out Handler<RoutingContext>>) {

    LOGIN          (GET, "/login",   LoginHandler::class.java),
    LOGIN_CALLBACK (GET, "/logincb", LoginCBHandler::class.java),

    PLAYLISTS       (GET, "/playlists",                PlaylistsHandler::class.java),
    PLAYLIST_TRACKS (GET, "/playlist/:uid/:pl/tracks", PlaylistTracksHandler::class.java),

    PLAYLIST         (GET,  "/playlist/:uid/:pl", PlaylistHandler::class.java),
    CREATE_PLAYLIST  (POST, "/playlist/:uid",     CreatePlaylistHandler::class.java),
    SHUFFLE_PLAYLIST (POST, "/playlist/:uid/:pl", CreateShuffledPlaylist::class.java)
}