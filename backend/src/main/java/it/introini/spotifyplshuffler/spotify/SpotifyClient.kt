package it.introini.spotifyplshuffler.spotify

import com.google.inject.ImplementedBy
import io.vertx.core.Future
import io.vertx.core.http.HttpClient
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import it.introini.spotifyplshuffler.manager.token.Token


@ImplementedBy(SpotifyClientImpl::class)
interface SpotifyClient {

    fun getAuthorizeUrl(state: String): String
    fun requestTokens(code: String, future: Future<JsonObject>)
    fun refreshToken(token: Token): Pair<Throwable?, JsonObject?>
    fun getMe(token: Token, future: Future<SpotifyUser>)
    fun getPlaylist(token: Token, uid: String, pid: String): Future<PlaylistFull>
    fun getPlaylists(token: Token, future: Future<PagingObject<SpotifyPlaylist>>)
    fun getPlaylistTracks(token: Token, uid: String, playlist: String, future: Future<Collection<SpotifyPlaylistTrack>>)
    fun createPlaylist(token: Token, uid: String, name: String, public: Boolean = false, collaborative: Boolean = false, description: String = ""): Future<SpotifyPlaylistFull>
    fun addTracks(token: Token, uid: String, pid: String, trackIds: Collection<String>): Future<JsonArray>
    fun getDevices(token: Token): Future<SpotifyDevices>
    fun getCurrentPlayback(token: Token): Future<SpotifyCurrentPlayingContext>
    fun startPlayback(token: Token, deviceId: String?): Future<Void>
    fun stopPlayback(token: Token, deviceId: String?): Future<Void>
    fun skipToNextPlayback(token: Token, deviceId: String?): Future<Void>
    fun skipToPrevPlayback(token: Token, deviceId: String?): Future<Void>
}