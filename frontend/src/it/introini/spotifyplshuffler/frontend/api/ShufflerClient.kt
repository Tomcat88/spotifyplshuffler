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
}

fun getAuthHeader(userId: String): Pair<String, String> {
    return "Authorization" to "Basic ${window.btoa("$userId:")}"
}

suspend fun <T> getAndParseResult(url: String, auth: Pair<String, String>, body: dynamic, parse: (dynamic) -> T): T =
        requestAndParseResult("GET", url, auth, body, parse)

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