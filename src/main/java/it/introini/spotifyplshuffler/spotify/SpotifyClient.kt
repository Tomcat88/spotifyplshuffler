package it.introini.spotifyplshuffler.spotify

import com.fasterxml.jackson.core.type.TypeReference
import com.google.inject.Inject
import io.netty.handler.codec.http.HttpResponseStatus
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
            if (it.statusCode() == HttpResponseStatus.OK.code()) {
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

    fun getPlaylistTracks(token: Token, playlist: String, future: Future<PagingObject<SpotifyPlaylistTrack>>) {
        val spotifyUser = token.spotifyUser
        if (spotifyUser == null) {
            future.fail("Spotify user not found in token!")
        } else {
            val formattedUrl = ME_PLAYLIST_TRACKS.replace("{user_id}", spotifyUser)
                                                 .replace("{playlist_id}", playlist)
            getRequest(formattedUrl, token, future, object : TypeReference<PagingObject<SpotifyPlaylistTrack>>() {})
        }
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
        val req = httpClient.getAbs(url) {
            if (it.statusCode() == HttpResponseStatus.OK.code()) {
                it.bodyHandler {
                    if (typeReference == null) {
                        future.complete(it.toJsonObject().mapTo(T::class.java))
                    } else {
                        future.complete(Json.mapper.convertValue(it.toJsonObject().map, typeReference))
                    }
                }
            } else {
                it.bodyHandler {
                    future.fail(it.toJsonObject().mapTo(SpotifyApiException::class.java))
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


}