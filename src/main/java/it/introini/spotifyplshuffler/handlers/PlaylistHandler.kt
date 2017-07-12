package it.introini.spotifyplshuffler.handlers

import com.google.inject.Inject
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import it.introini.spotifyplshuffler.manager.token.TokenManager
import it.introini.spotifyplshuffler.spotify.PagingObject
import it.introini.spotifyplshuffler.spotify.SpotifyClient
import it.introini.spotifyplshuffler.spotify.SpotifyPlaylist
import java.util.*


class PlaylistHandler @Inject constructor(val spotifyClient: SpotifyClient,
                                          val tokenManager: TokenManager): Handler<RoutingContext> {

    override fun handle(event: RoutingContext) {
        val auth = event.request().getHeader(HttpHeaderNames.AUTHORIZATION)?.removePrefix("Basic ")
        val token = auth?.let {
            String(Base64.getDecoder().decode(it)).split(":").component2()
        }?.let {
            tokenManager.getToken(it)
        }
        //TODO Check if token is expired
        if (token == null) {
            event.fail(HttpResponseStatus.UNAUTHORIZED.code())
        } else {
            val future = Future.future<PagingObject<SpotifyPlaylist>>()
            spotifyClient.getPlaylists(token, future)
            future.setHandler {
                if (it.succeeded()) {
                    event.response().end(it.result().items.map { JsonObject.mapFrom(it) }.let { JsonArray(it) }.encode())
                } else {
                    event.fail(it.cause())
                }
            }
        }
    }
}