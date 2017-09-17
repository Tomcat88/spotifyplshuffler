package it.introini.spotifyplshuffler.handlers

import com.google.inject.Inject
import io.netty.handler.codec.http.HttpHeaderNames
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import it.introini.spotifyplshuffler.manager.token.TokenManager
import it.introini.spotifyplshuffler.routes.AbstractHandler
import it.introini.spotifyplshuffler.spotify.SpotifyApiException
import it.introini.spotifyplshuffler.spotify.SpotifyClient


enum class PlaybackOp {
    START,
    STOP,
    NEXT,
    PREV
}

class PlaybackControlHandler @Inject constructor(spotifyClient: SpotifyClient,
                                                 tokenManager: TokenManager): AbstractHandler(tokenManager, spotifyClient) {
    override fun handle(event: RoutingContext) {
        val token = checkAuth(event)
        if (token != null) {
            val deviceId = event.request().getParam("device_id")
            val op = event.pathParam("op").let { PlaybackOp.valueOf(it.toUpperCase()) }
            when (op) {
                PlaybackOp.START -> spotifyClient.startPlayback(token, deviceId).setHandler(futureHandler(event))
                PlaybackOp.STOP  -> spotifyClient.stopPlayback(token, deviceId).setHandler(futureHandler(event))
                PlaybackOp.NEXT  -> spotifyClient.skipToNextPlayback(token, deviceId).setHandler(futureHandler(event))
                PlaybackOp.PREV  -> spotifyClient.skipToPrevPlayback(token, deviceId).setHandler(futureHandler(event))
            }
        }
    }

    private fun futureHandler(event: RoutingContext) = Handler<AsyncResult<Void>> {
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