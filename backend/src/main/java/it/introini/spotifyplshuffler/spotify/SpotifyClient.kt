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

    private val AUTH_URL     = "https://accounts.spotify.com/authorize"
    private val TOKENS_URL   = "https://accounts.spotify.com/api/token"

    private val BASE_API_URL        = "https://api.spotify.com/v1"
    private val ME_URL              = "$BASE_API_URL/me"
    private val ME_PLAYLISTS        = "$BASE_API_URL/me/playlists"
    private val PLAYLIST            = "$BASE_API_URL/users/{user_id}/playlists/{playlist_id}"
    private val ME_PLAYLIST_TRACKS  = "$BASE_API_URL/users/{user_id}/playlists/{playlist_id}/tracks"
    private val CREATE_PLAYLIST     = "$BASE_API_URL/users/{user_id}/playlists"

    private val DEVICES = "$ME_URL/player/devices"
    private val PLAYBACK = "$ME_URL/player"
    private val START_PLAYBACK = "$PLAYBACK/play"
    private val PAUSE_PLAYBACK = "$PLAYBACK/pause"

    private val redirectUri: String = "http://localhost:8082/shuffler/api/v1/logincb"

    private val httpClient: HttpClient = vertx.createHttpClient()

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

    fun refreshToken(token: Token): Pair<Throwable?, JsonObject?> {
        val auth = getClientAuthorizationHeader()
        val encoder = QueryStringEncoder("")
        encoder.addParam("grant_type", "authorization_code")
        encoder.addParam("redirect_uri", redirectUri)
        encoder.addParam("code", token.refreshToken)
        val body = encoder.toString().substring(1) // Strips '?'
        val (_, response, result) = Fuel.post(TOKENS_URL).header(HttpHeaders.AUTHORIZATION.toString() to auth,
                                                                 HttpHeaders.CONTENT_TYPE.toString() to "application/x-www-form-urlencoded")
                                                                 .body(body)
                                                                 .timeout(5000)
                                                                 .responseString()
        result.let { (data, error) ->
            if (error != null) {
                try {
                    return Pair(String(error.errorData).let { JsonObject(it) }.mapTo(SpotifyApiException::class.java), null)
                } catch (t: Throwable) {
                    return Pair(t, null)
                }
            } else if (data != null) {
                return Pair(null, data.let { JsonObject(it) })
            } else {
                return Pair(IllegalStateException("data and error both null"), null)
            }
        }
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

    fun addTracks(token: Token, uid: String, pid: String, trackIds: Collection<String>): Future<JsonArray> {
        val future = Future.future<JsonArray>()
        val formattedUrl = ME_PLAYLIST_TRACKS.replace("{user_id}", uid)
                                             .replace("{playlist_id}", pid)
        var done = false
        val results = JsonArray()
        var toAdd = trackIds
        while (!done) {
            val data = JsonObject()
            data.put("uris", toAdd.take(100).toList().let { JsonArray(it) })
            toAdd = toAdd.drop(100)
            if (toAdd.isEmpty()) {
                done = true
            }
            postRequest<JsonObject>(formattedUrl, token, data).let { (error, data) ->
                if (error != null) {
                    future.fail(error)
                    done = true
                } else {
                    results.add(data)
                }
            }
        }
        future.complete(results)
        return future
    }

    fun getDevices(token: Token): Future<SpotifyDevices> {
        val future = Future.future<SpotifyDevices>()
        getRequest<SpotifyDevices>(DEVICES, token, object: TypeReference<SpotifyDevices>() {}).let {
            (error, data) ->
                if (error != null) {
                    future.fail(error)
                } else {
                    future.complete(data)
                }
        }
        return future
    }

    fun getCurrentPlayback(token: Token): Future<SpotifyCurrentPlayingContext> {
        return getRequest<SpotifyCurrentPlayingContext>(PLAYBACK, token).let {
            (error, data) -> if (error != null) Future.failedFuture(error) else Future.succeededFuture(data)
        }
    }

    fun startPlayback(token: Token, deviceId: String?): Future<Void> {
        return controlPlayback(START_PLAYBACK, token, deviceId)
    }

    fun stopPlayback(token: Token, deviceId: String?): Future<Void> {
        return controlPlayback(PAUSE_PLAYBACK, token, deviceId)
    }


    // private utils

    private fun controlPlayback(url: String, token: Token, deviceId: String?): Future<Void> {
        val devicePair = deviceId?.let { "device_id" to it }
        return putRequest(url, devicePair?.let { listOf(it) } ?: emptyList(),  token).let {
            (error, _) -> if (error != null) Future.failedFuture(error) else Future.succeededFuture()
        }
    }

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
            when {
                error != null -> return try {
                    Pair(String(error.errorData).let { JsonObject(it) }.mapTo(SpotifyApiException::class.java), null)
                } catch (t: Throwable) {
                    Pair(t, null)
                }
                data != null -> return try {
                    if (typeReference == null) {
                        Pair(null, data.let { JsonObject(it) }.mapTo(T::class.java))
                    } else {
                        Pair(null, Json.mapper.convertValue(data.let { JsonObject(it).map }, typeReference))
                    }
                } catch (t: Throwable) {
                    Logger.error(t, "Unknown exception")
                    Pair(t, null)
                }
                else -> return Pair(IllegalStateException("data and error both null"), null)
            }
        }
    }

    private fun putRequest(url: String, params: List<Pair<String, Any?>>? = null, token: Token): Pair<Throwable?, Boolean> {
        val auth = getTokenAuthorizationHeader(token.accessToken)
        Logger.info("Performing put request on endpoint $url")
        val (_, response, result) = Fuel.put(url, params).header(auth).timeout(5000).responseString()
        Logger.info("Received response ${response.httpStatusCode}")
        result.let { (data, error) ->
            return when {
                error != null -> try {
                    Pair(String(error.errorData).let { JsonObject(it) }.mapTo(SpotifyApiException::class.java), false)
                } catch (t: Throwable) {
                    Pair(t, false)
                }
                data != null -> try {
                    Pair(null, true)
                } catch (t: Throwable) {
                    Logger.error(t, "Unknown exception")
                    Pair(t, false)
                }
                else -> Pair(IllegalStateException("data and error both null"), false)
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
                        return Pair(null, data.let { JsonObject(it) }.let {  if (T::class != JsonObject::class) it.mapTo(T::class.java) else T::class.java.cast(it) })
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