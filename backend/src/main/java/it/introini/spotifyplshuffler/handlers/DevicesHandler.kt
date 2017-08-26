package it.introini.spotifyplshuffler.handlers

import com.google.inject.Inject
import io.netty.handler.codec.http.HttpHeaderNames
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import it.introini.spotifyplshuffler.manager.token.TokenManager
import it.introini.spotifyplshuffler.routes.AbstractHandler
import it.introini.spotifyplshuffler.spotify.SpotifyApiException
import it.introini.spotifyplshuffler.spotify.SpotifyClient


class DevicesHandler @Inject constructor(spotifyClient: SpotifyClient,
                                         tokenManager: TokenManager): AbstractHandler(tokenManager, spotifyClient) {
    override fun handle(event: RoutingContext) {
        val token = checkAuth(event)
        if (token != null) {
            spotifyClient.getDevices(token).setHandler {
                if (it.succeeded()) {
                    event.response().putHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                    event.response().end(it.result().devices.map { it.toJson() }.let { JsonArray(it) }.encode())
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