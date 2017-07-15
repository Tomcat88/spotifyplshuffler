package it.introini.spotifyplshuffler.handlers

import com.google.inject.Inject
import io.netty.handler.codec.http.HttpHeaderNames
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import it.introini.spotifyplshuffler.manager.token.TokenManager
import it.introini.spotifyplshuffler.routes.AbstractHandler
import it.introini.spotifyplshuffler.spotify.PagingObject
import it.introini.spotifyplshuffler.spotify.SpotifyApiException
import it.introini.spotifyplshuffler.spotify.SpotifyClient
import it.introini.spotifyplshuffler.spotify.SpotifyPlaylist


class PlaylistHandler @Inject constructor(val spotifyClient: SpotifyClient,
                                              tokenManager: TokenManager): AbstractHandler(tokenManager) {

    override fun handle(event: RoutingContext) {
        val token = checkAuth(event)
        if (token != null) {
            val future = Future.future<PagingObject<SpotifyPlaylist>>()
            spotifyClient.getPlaylists(token, future)
            future.setHandler { result ->
                if (result.succeeded()) {
                    event.response().putHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                    event.response().end(JsonObject.mapFrom(result.result()).encode())
                } else {
                    val cause = result.cause()
                    if (cause is SpotifyApiException) {
                        event.response().putHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                        event.response().statusCode = cause.error.status
                        event.response().end(JsonObject.mapFrom(cause.error).encode())
                    } else {
                        event.fail(cause)
                    }
                }
            }
        }
    }
}