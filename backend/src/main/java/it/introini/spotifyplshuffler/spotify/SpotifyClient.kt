package it.introini.spotifyplshuffler.spotify

import com.fasterxml.jackson.core.type.TypeReference
import com.github.kittinunf.fuel.Fuel
import com.google.inject.Inject
import io.netty.handler.codec.http.HttpResponseStatus.OK
import io.netty.handler.codec.http.QueryStringEncoder
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import it.introini.spotifyplshuffler.config.Config
import it.introini.spotifyplshuffler.config.Parameter
import it.introini.spotifyplshuffler.manager.token.Token
import org.pmw.tinylog.Logger
import java.net.URLEncoder
import java.util.*


class SpotifyClient @Inject constructor(val config: Config,
                                        val vertx: Vertx){

    val AUTH_URL     = "https://accounts.spotify.com/authorize"
    val TOKENS_URL   = "https://accounts.spotify.com/api/token"

    val BASE_API_URL        = "https://api.spotify.com/v1"
    val ME_URL              = "$BASE_API_URL/me"
    val ME_PLAYLISTS        = "$BASE_API_URL/me/playlists"
    val PLAYLIST            = "$BASE_API_URL/users/{user_id}/playlists/{playlist_id}"
    val ME_PLAYLIST_TRACKS  = "$BASE_API_URL/users/{user_id}/playlists/{playlist_id}/tracks"
    val CREATE_PLAYLIST     = "$BASE_API_URL/users/{user_id}/playlists"

    val redirectUri: String = "http://localhost:8082/shuffler/api/v1/logincb"

    val httpClient: HttpClient = vertx.createHttpClient()

    fun getAuthorizeUrl(state: String): String {
        val clientId = config.getString(Parameter.CLIENT_ID)
        val scopes = config.getString(Parameter.SCOPES)
        val encodedRedirectURI = URLEncoder.encode(redirectUri, "UTF-8")
        return "$AUTH_URL?response_type=code&client_id=$clientId&state=$state&scope=$scopes&redirect_uri=$encodedRedirectURI"
    }

    fun requestTokens(code: String, future: Future<JsonObject>) {
        val auth = getClientAuthorizationHeader()
        val request = httpClient.postAbs(TOKENS_URL) {
            if (it.statusCode() == OK.code()) {
                it.bodyHandler {
                    future.complete(it.toJsonObject())
                }
            } else {
                it.bodyHandler {
                    future.fail(it.toJsonObject().mapTo(SpotifyAuthException::class.java))
                }
            }
        }
        request.putHeader(HttpHeaders.AUTHORIZATION, auth)
        request.putHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
        val encoder = QueryStringEncoder("")
        encoder.addParam("grant_type", "authorization_code")
        encoder.addParam("redirect_uri", redirectUri)
        encoder.addParam("code", code)
        val body = encoder.toString().substring(1) // Strips '?'
        request.setTimeout(5000L)
        request.exceptionHandler {
            future.fail(it)
        }
        request.end(Buffer.buffer(body))
    }

    fun getMe(token: Token, future: Future<SpotifyUser>) {
        getRequest<SpotifyUser>(ME_URL, token).let {
            (error, data) ->
            if (error != null) {
                future.fail(error)
            } else {
                future.complete(data)
            }
        }
    }

    fun getPlaylist(token: Token, uid: String, pid: String): Future<PlaylistFull> {
        val future = Future.future<PlaylistFull>()
        val formattedUrl = PLAYLIST.replace("{user_id}", uid)
                                   .replace("{playlist_id}", pid)
        getRequest<SpotifyPlaylistFull>(formattedUrl, token).let {
            (error, data) ->
            if (error != null) {
                future.fail(error)
            } else if (data != null){
                if (data.tracks.next == null) {
                    future.complete(PlaylistFull(
                            data.collaborative,
                            data.description,
                            data.owner,
                            data.href,
                            data.id,
                            data.spotifyImages,
                            data.name,
                            data.public,
                            data.snapshotId,
                            data.type,
                            data.uri,
                            data.tracks.items
                    ))
                } else {
                    val tracks = mutableListOf<SpotifyPlaylistTrack>()
                    tracks.addAll(data.tracks.items)
                    var exit = false
                    var url = data.tracks.next
                    while (!exit) {
                        getRequest<PagingObject<SpotifyPlaylistTrack>>(url!!, token, object : TypeReference<PagingObject<SpotifyPlaylistTrack>>() {}).let {
                            (error, trackData) ->
                                if (error != null) {
                                    future.fail(error)
                                    exit = true
                                } else {
                                    tracks.addAll(trackData?.items ?: emptyList())
                                    if (trackData?.next == null) {
                                        future.complete(PlaylistFull(
                                                data.collaborative,
                                                data.description,
                                                data.owner,
                                                data.href,
                                                data.id,
                                                data.spotifyImages,
                                                data.name,
                                                data.public,
                                                data.snapshotId,
                                                data.type,
                                                data.uri,
                                                tracks
                                        ))
                                        exit = true
                                    } else {
                                        url = trackData.next
                                    }
                                }
                        }
                    }
                }
            }
        }
        return future
    }

    fun getPlaylists(token: Token, future: Future<PagingObject<SpotifyPlaylist>>) {
        getRequest<PagingObject<SpotifyPlaylist>>(ME_PLAYLISTS,
                                                  token,
                                                  object : TypeReference<PagingObject<SpotifyPlaylist>>() {}).let {
            (error, data) ->
            if (error != null) {
                future.fail(error)
            } else {
                future.complete(data)
            }
        }
    }

    fun getPlaylistTracks(token: Token, uid: String, playlist: String, future: Future<Collection<SpotifyPlaylistTrack>>) {
        val formattedUrl = ME_PLAYLIST_TRACKS.replace("{user_id}", uid)
                                             .replace("{playlist_id}", playlist)
        val tracks = mutableListOf<SpotifyPlaylistTrack>()
        var exit = false
        var url = formattedUrl
        while (!exit) {
            getRequest<PagingObject<SpotifyPlaylistTrack>>(url,
                    token,
                    object : TypeReference<PagingObject<SpotifyPlaylistTrack>>() {}).let {
                (error, data) ->
                if (error != null) {
                    future.fail(error)
                    exit = true
                } else {
                    tracks.addAll(data?.items ?: emptyList())
                    if (data?.next == null) {
                        future.complete(tracks)
                        exit = true
                    } else {
                        url = data.next
                    }
                }
            }
        }
    }

    fun createPlaylist(token: Token, uid: String, name: String, public: Boolean = false, collaborative: Boolean = false, description: String = ""): Future<SpotifyPlaylistFull> {
        val future = Future.future<SpotifyPlaylistFull>()
        val data = JsonObject()
        data.put("name", name)
        data.put("public", public)
        data.put("collaborative", collaborative)
        data.put("description", description)
        val formattedUrl = CREATE_PLAYLIST.replace("{user_id}", uid)
        postRequest<SpotifyPlaylistFull>(formattedUrl, token, data).let {
            (error, data) ->
            if (error != null) {
                future.fail(error)
            } else {
                future.complete(data)
            }
        }
        return future
    }

    fun addTracks(token: Token, uid: String, pid: String, trackIds: Collection<String>): Future<JsonObject> {
        val future = Future.future<JsonObject>()
        val data = JsonObject()
        data.put("uris", trackIds.toList().let { JsonArray(it) })
        val formattedUrl = ME_PLAYLIST_TRACKS.replace("{user_id}", uid)
                                             .replace("{playlist_id", pid)

        postRequest<JsonObject>(formattedUrl, token, data).let {
            (error, data) ->
            if (error != null) {
                future.fail(error)
            } else {
                future.complete(data)
            }
        }
        return future
    }

    // private utils

    private fun getTokenAuthorizationHeader(accessToken: String): Pair<String, String> {
        return HttpHeaders.AUTHORIZATION.toString() to "Bearer $accessToken"
    }

    private fun getClientAuthorizationHeader(): String {
        val clientId     = config.getString(Parameter.CLIENT_ID)
        val clientSecret = config.getString(Parameter.CLIENT_SECRET)
        val auth = "$clientId:$clientSecret"
        val base64 = Base64.getEncoder().encodeToString(auth.toByteArray())
        return "Basic $base64"
    }

    inline private fun <reified T> getRequest(url: String, token: Token, typeReference: TypeReference<*>? = null): Pair<Throwable?, T?> {
        val auth = getTokenAuthorizationHeader(token.accessToken)
        Logger.info("Performing get request on endpoint $url")
        val (_, response, result) = Fuel.get(url).header(auth).timeout(5000).responseString()
        Logger.info("Received response ${response.httpStatusCode}")
        result.let { (data, error) ->
            if (error != null) {
                try {
                    return Pair(String(error.errorData).let { JsonObject(it) }.mapTo(SpotifyApiException::class.java), null)
                } catch (t: Throwable) {
                    return Pair(t, null)
                }
            } else if (data != null) {
                try {
                    if (typeReference == null) {
                        return Pair(null, data.let { JsonObject(it) }.mapTo(T::class.java))
                    } else {
                        return Pair(null, Json.mapper.convertValue(data.let { JsonObject(it).map }, typeReference))
                    }
                } catch (t: Throwable) {
                    Logger.error(t, "Unknown exception")
                    return Pair(t, null)
                }
            } else {
                return Pair(IllegalStateException("data and error both null"), null)
            }
        }
    }

    inline private fun <reified T> postRequest(url: String, token: Token, dataObject: JsonObject, typeReference: TypeReference<*>? = null): Pair<Throwable?, T?> {
        val auth = getTokenAuthorizationHeader(token.accessToken)
        Logger.info("Performing post request on endpoint $url")
        val (_, response, result) = Fuel.post(url).header(auth, HttpHeaders.CONTENT_TYPE.toString() to "application/json").body(dataObject.encode()).timeout(5000).responseString()
        Logger.info("Received response ${response.httpStatusCode}")
        result.let { (data, error) ->
            if (error != null) {
                try {
                    return Pair(String(error.errorData).let { JsonObject(it) }.mapTo(SpotifyApiException::class.java), null)
                } catch (t: Throwable) {
                    return Pair(t, null)
                }
            } else if (data != null) {
                try {
                    if (typeReference == null) {
                        return Pair(null, data.let { JsonObject(it) }.mapTo(T::class.java))
                    } else {
                        return Pair(null, Json.mapper.convertValue(data.let { JsonObject(it).map }, typeReference))
                    }
                } catch (t: Throwable) {
                    Logger.error(t, "Unknown exception")
                    return Pair(t, null)
                }
            } else {
                return Pair(IllegalStateException("data and error both null"), null)
            }
        }
    }

}