package it.introini.spotifyplshuffler.handlers

import com.google.inject.Inject
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import it.introini.spotifyplshuffler.manager.token.TokenManager
import it.introini.spotifyplshuffler.routes.AbstractHandler
import it.introini.spotifyplshuffler.spotify.PagingObject
import it.introini.spotifyplshuffler.spotify.SpotifyClient
import it.introini.spotifyplshuffler.spotify.SpotifyPlaylistTrack
import org.pmw.tinylog.Logger

class PlaylistTracksHandler @Inject constructor(val spotifyClient: SpotifyClient,
                                                    tokenManager: TokenManager) : AbstractHandler(tokenManager) {

    override fun handle(event: RoutingContext) {
        val token = checkAuth(event)
        if (token != null) {
            val pl = event.pathParam("pl")
            val userId = event.pathParam("userId")
            if (pl == null) {
                event.response().statusCode = HttpResponseStatus.BAD_REQUEST.code()
                event.response().end(JsonObject().put("error", "pl is mandatory").encode())
            } else if (userId == null) {
                event.response().statusCode = HttpResponseStatus.BAD_REQUEST.code()
                event.response().end(JsonObject().put("error", "user is mandatory").encode())
            } else {
                val future = Future.future<PagingObject<SpotifyPlaylistTrack>>()
                spotifyClient.getPlaylistTracks(token, userId, pl, future)
                future.setHandler {
                    if (it.succeeded()) {
                        Logger.info(it.result())
                        event.response().end()
                    } else {
                        event.fail(it.cause())
                    }
                }
            }
        }
    }
}