package it.introini.spotifyplshuffler.handlers

import com.google.inject.Inject
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import it.introini.spotifyplshuffler.manager.token.TokenManager
import it.introini.spotifyplshuffler.routes.AbstractHandler
import it.introini.spotifyplshuffler.spotify.SpotifyClient
import java.util.*


class CreateShuffledPlaylist @Inject constructor(val spotifyClient: SpotifyClient,
                                                     tokenManager: TokenManager): AbstractHandler(tokenManager) {
    override fun handle(event: RoutingContext) {
        val token = checkAuth(event)
        if (token != null) {
            val pl = event.pathParam("pl")
            val uid = event.pathParam("uid")
            if (token.spotifyUser == null) {
                event.response().statusCode = HttpResponseStatus.BAD_REQUEST.code()
                event.response().end(JsonObject().put("error", "token not associated with valid spotify user").encode())
            } else if (pl == null || uid == null) {
                event.response().statusCode = HttpResponseStatus.BAD_REQUEST.code()
                event.response().end(JsonObject().put("error", "uid/pl is mandatory").encode())
            } else {
                spotifyClient.getPlaylist(token, uid, pl).compose { playlist ->
                    spotifyClient.createPlaylist(token, token.spotifyUser!!, "${playlist.name} (Shuffled)").compose { newPlaylist ->
                        val tracks = playlist.tracks.map { it.track.uri }.toMutableList()
                        Collections.shuffle(tracks)
                        spotifyClient.addTracks(token, uid, newPlaylist.id, tracks)
                    }
                }.setHandler {
                    if (it.succeeded()) {
                        //TODO decide what to return
                    } else {
                        
                    }
                }
            }
        }
    }
}