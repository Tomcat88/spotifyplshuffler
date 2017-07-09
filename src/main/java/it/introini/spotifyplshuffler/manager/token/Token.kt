package it.introini.spotifyplshuffler.manager.token

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant


data class Token(@JsonProperty("token_type")    val tokenType: String,
                 @JsonProperty("access_token")  val accessToken: String,
                 @JsonProperty("refresh_token") val refreshToken: String,
                 @JsonProperty("expires_in")    val expiresIn: Long,
                 @JsonProperty("scope")         val scope: String,
                 @JsonProperty("created_on")    val createdOn: Instant,
                 @JsonProperty("user_id")       val userId: String) {

    fun getExpireTime() = createdOn.plusSeconds(expiresIn)
}