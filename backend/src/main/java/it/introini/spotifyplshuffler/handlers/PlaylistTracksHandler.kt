package it.introini.spotifyplshuffler.handlers

import com.google.inject.Inject
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Future
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import it.introini.spotifyplshuffler.manager.token.TokenManager
import it.introini.spotifyplshuffler.routes.AbstractHandler
import it.introini.spotifyplshuffler.spotify.SpotifyApiException
import it.introini.spotifyplshuffler.spotify.SpotifyClient
import it.introini.spotifyplshuffler.spotify.SpotifyPlaylistTrack
import org.pmw.tinylog.Logger

class PlaylistTracksHandler @Inject constructor(spotifyClient: SpotifyClient,
                                                tokenManager: TokenManager) : AbstractHandler(tokenManager, spotifyClient) {

    override fun handle(event: RoutingContext) {
        val token = checkAuth(event)
        if (token != null) {
            val pl = event.pathParam("pl")
            val uid = event.pathParam("uid")
            if (pl == null || uid == null) {
                event.response().statusCode = HttpResponseStatus.BAD_REQUEST.code()
                event.response().end(JsonObject().put("error", "pl/uid is mandatory").encode())
            } else {
                val future = Future.future<Collection<SpotifyPlaylistTrack>>()
                spotifyClient.getPlaylistTracks(token, uid, pl, future)
                future.setHandler {
                    if (it.succeeded()) {
                        Logger.info(it.result())
                        event.response().putHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                        event.response().end(it.result().map { it.toJson() }.let { JsonArray(it) }.encode())
                    } else {
                        val cause = it.cause()
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
}