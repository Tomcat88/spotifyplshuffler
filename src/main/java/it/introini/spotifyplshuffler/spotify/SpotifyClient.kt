package it.introini.spotifyplshuffler.spotify

import com.fasterxml.jackson.core.type.TypeReference
import com.google.inject.Inject
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpResponseStatus.*
import io.netty.handler.codec.http.QueryStringEncoder
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.Json
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

    val BASE_API_URL = "https://api.spotify.com/v1"
    val ME_URL       = "$BASE_API_URL/me"
    val ME_PLAYLISTS = "$BASE_API_URL/me/playlists"
    val ME_PLAYLIST_TRACKS = "$BASE_API_URL/users/{user_id}/playlists/{playlist_id}/tracks"

    val CREATE_PLAYLIST = "$BASE_API_URL/users/{user_id}/playlists"

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
        getRequest(ME_URL, token, future)
    }

    fun getPlaylists(token: Token, future: Future<PagingObject<SpotifyPlaylist>>) {
        getRequest(ME_PLAYLISTS, token, future, object : TypeReference<PagingObject<SpotifyPlaylist>>() {})
    }

    fun getPlaylistTracks(token: Token, uid: String, playlist: String, future: Future<PagingObject<SpotifyPlaylistTrack>>) {
        val formattedUrl = ME_PLAYLIST_TRACKS.replace("{user_id}", uid)
                                             .replace("{playlist_id}", playlist)
        getRequest(formattedUrl, token, future, object : TypeReference<PagingObject<SpotifyPlaylistTrack>>() {})
    }

    fun createPlaylist(token: Token, uid: String, name: String, public: Boolean?, collaborative: Boolean?, description: String?, future: Future<SpotifyPlaylistFull>) {
        val data = JsonObject()
        data.put("name", name)
        data.put("public", public ?: false)
        data.put("collaborative", collaborative ?: false)
        data.put("description", description ?: name)
        val formattedUrl = CREATE_PLAYLIST.replace("{user_id}", uid)
        postRequest(formattedUrl, token, data, future)
    }

    // private utils

    private fun getTokenAuthorizationHeader(accessToken: String): String {
        return "Bearer $accessToken"
    }

    private fun getClientAuthorizationHeader(): String {
        val clientId     = config.getString(Parameter.CLIENT_ID)
        val clientSecret = config.getString(Parameter.CLIENT_SECRET)
        val auth = "$clientId:$clientSecret"
        val base64 = Base64.getEncoder().encodeToString(auth.toByteArray())
        return "Basic $base64"
    }

    inline private fun <reified T> getRequest(url: String, token: Token, future: Future<T>, typeReference: TypeReference<*>? = null) {
        val auth = getTokenAuthorizationHeader(token.accessToken)
        Logger.info("Performing get request on endpoint $url")
        val req = httpClient.getAbs(url) {
            Logger.info("Received response ${it.statusCode()}")
            if (it.statusCode() == OK.code() ||
                it.statusCode() == CREATED.code() ||
                it.statusCode() == ACCEPTED.code()) {
                it.bodyHandler {
                    try {
                        if (typeReference == null) {
                            future.complete(it.toJsonObject().mapTo(T::class.java))
                        } else {
                            future.complete(Json.mapper.convertValue(it.toJsonObject().map, typeReference))
                        }
                    } catch (t: Throwable) {
                        Logger.error(t, "Unknown exception")
                        future.fail(INTERNAL_ERROR)
                    }
                }
            } else {
                it.bodyHandler {
                    try {
                        future.fail(it.toJsonObject().mapTo(SpotifyApiException::class.java))
                    } catch (t: Throwable) {
                        future.fail(t)
                    }
                }
            }
        }
        req.exceptionHandler {
            future.fail(it)
        }
        req.setTimeout(5000L)
        req.putHeader(HttpHeaders.AUTHORIZATION, auth)
        req.end()
    }

    inline private fun <reified T> postRequest(url: String, token: Token, dataObject: JsonObject, future: Future<T>, typeReference: TypeReference<*>? = null) {
        val auth = getTokenAuthorizationHeader(token.accessToken)
        Logger.info("Performing post request on endpoint $url")
        val req = httpClient.postAbs(url) {
            Logger.info("Received response ${it.statusCode()}")
            if (it.statusCode() == OK.code() ||
                it.statusCode() == CREATED.code() ||
                it.statusCode() == ACCEPTED.code()) {
                it.bodyHandler {
                    try {
                        if (typeReference == null) {
                            future.complete(it.toJsonObject().mapTo(T::class.java))
                        } else {
                            future.complete(Json.mapper.convertValue(it.toJsonObject().map, typeReference))
                        }
                    } catch (t: Throwable) {
                        Logger.error(t, "Unknown exception")
                        future.fail(INTERNAL_ERROR)
                    }
                }
            } else {
                it.bodyHandler {
                    try {
                        future.fail(it.toJsonObject().mapTo(SpotifyApiException::class.java))
                    } catch (t: Throwable) {
                        future.fail(t)
                    }
                }
            }
        }

        req.exceptionHandler {
            future.fail(it)
        }
        req.setTimeout(5000L)
        req.putHeader(HttpHeaders.AUTHORIZATION, auth)
        req.putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        req.end(dataObject.encode())

    }


}