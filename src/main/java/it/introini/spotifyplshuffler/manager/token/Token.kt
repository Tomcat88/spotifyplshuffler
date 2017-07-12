package it.introini.spotifyplshuffler.manager.token

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant


@JsonIgnoreProperties(ignoreUnknown = true)
data class Token(@JsonProperty("tokenType")    val tokenType: String,
                 @JsonProperty("accessToken")  val accessToken: String,
                 @JsonProperty("refreshToken") val refreshToken: String,
                 @JsonProperty("expiresIn")    val expiresIn: Long,
                 @JsonProperty("scope")        val scope: String,
                 @JsonProperty("createdOn")    val createdOn: Instant,
                 @JsonProperty("userId")       val userId: String) {

    fun getExpireTime() = createdOn.plusSeconds(expiresIn)
}