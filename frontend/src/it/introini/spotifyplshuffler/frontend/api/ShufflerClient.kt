package it.introini.spotifyplshuffler.frontend.api

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.await
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import kotlin.browser.window
import kotlin.js.json

val BASE_API = "/shuffler/api/v1"
val LOGIN_ENDPOINT = "$BASE_API/login"

open class ShufflerClient {
    companion object: ShufflerClient()

    fun login() {
        window.location.href = LOGIN_ENDPOINT
    }

    suspend fun getPlaylist(userId: String): PagingObject<SpotifyPlaylist> {
        val auth = getAuthHeader(userId)
        return async {
            getAndParseResult("$BASE_API/playlists", auth, null, { parsePagingObject(it, this::parseSpotifyPlaylist) })
        }.await()
    }

    suspend fun getPlaylistTracks(userId: String, pl: String, uid: String): Collection<SpotifyPlaylistTrack> {
        val auth = getAuthHeader(userId)
        return async {
            getAndParseResult("$BASE_API/playlist/$uid/$pl/tracks", auth, null, this::parseSpotifyPlaylistTracks)
        }.await()
    }

    suspend fun shufflePlaylist(userId: String, pl: String, uid: String): Boolean {
        val auth = getAuthHeader(userId)
        return async {
            postAndParseResult("$BASE_API/playlist/$uid/$pl/shuffle", auth, null, { it != null })
        }.await()
    }

    suspend fun getDevices(userId: String): Collection<SpotifyDevice> {
        val auth = getAuthHeader(userId)
        return async {
            getAndParseResult("$BASE_API/devices", auth, null, this::parseSpotifyDevices)
        }.await()
    }

    suspend fun getPlayback(userId: String): SpotifyCurrentPlayingContext {
        val auth = getAuthHeader(userId)
        return async {
            getAndParseResult("$BASE_API/playback", auth, null, this::parseSpotifyCurrentPlayingContext)
        }.await()
    }

    suspend fun playbackControl(userId: String, op: String, deviceId: String? = null): Boolean {
        val auth = getAuthHeader(userId)
        val deviceIdParam = (deviceId?.let { "?device_id=$it" }) ?: ""
        return async {
            getAndParseResult("$BASE_API/playback/control/$op$deviceIdParam", auth, null, { it != null })
        }.await()
    }

    suspend fun shufflePlayback(userId: String, deviceId: String? = null, shuffle: Boolean): Boolean {
        val auth = getAuthHeader(userId)
        val stateParam = "?state=$shuffle"
        val deviceIdParam = (deviceId?.let { "&device_id=$it" }) ?: ""
        return async {
            getAndParseResult("$BASE_API/playback/shuffle$stateParam$deviceIdParam", auth, null, { it != null })
        }.await()
    }

    // private utils

    private fun parseSpotifyPlaylistTracks(json: dynamic): Collection<SpotifyPlaylistTrack> {
        val array = json as Array<dynamic>
        return array.map { t ->
            SpotifyPlaylistTrack(
                    t.addedAt,
                    t.isLocal,
                    parseSpotifyTrack(t.track)
            )
        }
    }

    private fun parseSpotifyTrack(json: dynamic): SpotifyTrack? {
        if (json == null) return null
        val artists = json.artists as Array<dynamic>
        val markets = json.availableMarkets as Array<dynamic>
        return SpotifyTrack(
                json.id,
                json.name,
                artists.map(this::parseSpotifyArtist),
                markets.map(Any::toString),
                json.discNumber,
                json.durationMs,
                json.explicit,
                json.href,
                json.isPlayable,
                json.popularity,
                json.previewUrl,
                json.trackNumber,
                json.type,
                json.uri
        )
    }

    private fun parseSpotifyArtist(json: dynamic): SpotifyArtist {
        return SpotifyArtist(
                json.id,
                json.href,
                json.name,
                json.type,
                json.uri
        )
    }

    private fun parseSpotifyPublicUser(json: dynamic): SpotifyPublicUser {
        val images = json.spotifyImages as Array<dynamic>?
        return SpotifyPublicUser(
                json.displayName,
                json.id,
                json.type,
                json.uri,
                images?.map(this::parseSpotifyImage)
        )
    }

    private fun parseSpotifyImage(json: dynamic): SpotifyImage {
        return SpotifyImage(json.height, json.width, json.url)
    }

    private fun parseSpotifyPlaylist(json: dynamic): SpotifyPlaylist {
        val images = json.spotifyImages as Array<SpotifyImage>
        return SpotifyPlaylist(
                json.collaborative,
                json.description,
                parseSpotifyPublicUser(json.owner),
                json.href,
                json.id,
                images.map(this::parseSpotifyImage),
                json.name,
                json.public,
                json.snapshotId,
                json.type,
                json.uri,
                parseTracksObject(json.tracks)
        )
    }

    private fun parseTracksObject(json: dynamic): TracksObject {
        return TracksObject(json.href, json.total)
    }

    private fun <T> parsePagingObject(json: dynamic, parse: (dynamic) -> T): PagingObject<T> {
        val items = json.items as Array<T>
        return PagingObject(
                items.map(parse),
                json.href,
                json.limit,
                json.next,
                json.offset,
                json.previous,
                json.total
        )
    }

    private fun parseSpotifyDevices(json: dynamic): Collection<SpotifyDevice> {
       val array = json as Array<dynamic>
       return array.mapNotNull { parseSpotifyDevice(it) }
    }

    private fun parseSpotifyDevice(json: dynamic): SpotifyDevice? {
        if (json == null) return null
        return SpotifyDevice(
                json.id,
                json.isActive,
                json.isRestricted,
                json.name,
                json.type,
                json.volumePercent
        )
    }

    private fun parseSpotifyCurrentPlayingContext(json: dynamic): SpotifyCurrentPlayingContext {
        console.log(json)
        return SpotifyCurrentPlayingContext(
            parseSpotifyDevice(json.device),
            json.repeatState,
            json.shuffleState,
            parseContextObject(json.context),
            json.timestamp,
            json.progressMs,
            json.playing,
            parseSpotifyTrack(json.item)
        )
    }

    private fun parseContextObject(json: dynamic): SpotifyContextObject? {
        if (json == null) return null
        return SpotifyContextObject(
                json.uri,
                json.href,
                json.type
        )
    }
}

fun getAuthHeader(userId: String): Pair<String, String> {
    return "Authorization" to "Basic ${window.btoa("$userId:")}"
}

suspend fun <T> getAndParseResult(url: String, auth: Pair<String, String>, body: dynamic, parse: (dynamic) -> T): T =
        requestAndParseResult("GET", url, auth, body, parse)

suspend fun <T> postAndParseResult(url: String, auth: Pair<String, String>, body: dynamic, parse: (dynamic) -> T): T =
        requestAndParseResult("POST", url, auth, body, parse)

suspend fun <T> requestAndParseResult(method: String, url: String, auth: Pair<String, String>, body: dynamic, parse: (dynamic) -> T): T {
    val headers = arrayOf(
            auth,
            "Accept" to "application/json"
    )
    val response = window.fetch(url, object: RequestInit {
        override var method: String? = method
        override var body: dynamic = body
        override var credentials: RequestCredentials? = "same-origin".asDynamic()
        override var headers: dynamic = json(*headers)
    }).await()
    return parse(response.json().await())
}