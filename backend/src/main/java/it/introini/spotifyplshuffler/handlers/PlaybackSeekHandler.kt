package it.introini.spotifyplshuffler.handlers

import com.google.inject.Inject
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import it.introini.spotifyplshuffler.manager.token.TokenManager
import it.introini.spotifyplshuffler.routes.AbstractHandler
import it.introini.spotifyplshuffler.spotify.SpotifyApiException
import it.introini.spotifyplshuffler.spotify.SpotifyClient


class PlaybackSeekHandler @Inject constructor(tokenManager: TokenManager,
                                              spotifyClient: SpotifyClient) : AbstractHandler(tokenManager, spotifyClient) {
    override fun handle(event: RoutingContext) {
        val token = checkAuth(event)
        if (token != null) {
            val deviceId = event.request().getParam("device_id")
            val positionMs = event.request().getParam("position_ms")?.toInt()
            if (positionMs == null) {
                event.response().statusCode = HttpResponseStatus.BAD_REQUEST.code()
                event.response().end(JsonObject().put("error", "position_ms is mandatory").encode())
            } else {
                spotifyClient.seekPlayback(token, deviceId, positionMs).setHandler {
                    if (it.succeeded()) {
                        event.response().putHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                        event.response().end(JsonObject().encode())
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