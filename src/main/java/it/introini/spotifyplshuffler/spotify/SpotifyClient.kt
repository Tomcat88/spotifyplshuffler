package it.introini.spotifyplshuffler.spotify

import com.google.inject.Inject
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.QueryStringEncoder
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClient
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import it.introini.spotifyplshuffler.config.Config
import it.introini.spotifyplshuffler.config.Parameter
import it.introini.spotifyplshuffler.manager.token.Token
import java.net.URLEncoder
import java.util.*


class SpotifyClient @Inject constructor(val config: Config,
                                        val vertx: Vertx){

    val AUTH_URL   = "https://accounts.spotify.com/authorize"
    val TOKENS_URL = "https://accounts.spotify.com/api/token"
    val ME_URL     = "https://api.spotify.com/v1/me"

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
                    future.fail("Error while requesting tokens $it")
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
        val auth = getTokenAuthorizationHeader(token.accessToken)
        val req = httpClient.getAbs(ME_URL) {
            if (it.statusCode() == HttpResponseStatus.OK.code()) {
                it.bodyHandler {
                    future.complete(it.toJsonObject().mapTo(SpotifyUser::class.java))
                }
            } else {
                it.bodyHandler {
                    future.fail("Error while requesting user $it")
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
}