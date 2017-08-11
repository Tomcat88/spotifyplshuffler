package it.introini.spotifyplshuffler.handlers

import com.google.inject.Inject
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import it.introini.spotifyplshuffler.manager.token.TokenManager
import it.introini.spotifyplshuffler.routes.AbstractHandler
import it.introini.spotifyplshuffler.spotify.SpotifyApiException
import it.introini.spotifyplshuffler.spotify.SpotifyClient
import it.introini.spotifyplshuffler.spotify.SpotifyPlaylistFull
import org.pmw.tinylog.Logger


class CreatePlaylistHandler @Inject constructor(tokenManager: TokenManager,
                                                val spotifyClient: SpotifyClient): AbstractHandler(tokenManager) {
    override fun handle(event: RoutingContext) {
        val token = checkAuth(event)
        if (token != null) {
            val data = event.bodyAsJson
            val uid = event.pathParam("uid")
            if (!data.containsKey("name") || uid == null) {
                event.response().statusCode = HttpResponseStatus.BAD_REQUEST.code()
                event.response().end(JsonObject().put("error", "name/uid is mandatory").encode())
            } else {
                val name = data.getString("name")
                val public = data.getBoolean("public")
                val collaborative = data.getBoolean("collaborative")
                val description = data.getString("description")
                val future = spotifyClient.createPlaylist(token, uid, name, public, collaborative, description)
                future.setHandler {
                    if (it.succeeded()) {
                        Logger.info("Playlist created, {}", it.result().encode())
                        event.response().putHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                        event.response().end(it.result().encode())
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