package it.introini.spotifyplshuffler.manager.token

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import it.introini.spotifyplshuffler.spotify.Encodable
import java.time.Instant


@JsonIgnoreProperties(ignoreUnknown = true)
class Token(@JsonProperty("token_type")    tokenType: String,
            @JsonProperty("access_token")  accessToken: String,
            @JsonProperty("refresh_token") refreshToken: String,
            @JsonProperty("expires_n")     expiresIn: Long,
            @JsonProperty("scope")         scope: String,
            @JsonProperty("created_on")    createdOn: Instant,
            @JsonProperty("user_id")       userId: String): Encodable {

    val tokenType:    String   = tokenType    @JsonProperty("token_type")    get
    val accessToken:  String   = accessToken  @JsonProperty("access_token")  get
    val refreshToken: String   = refreshToken @JsonProperty("refresh_token") get
    val expiresIn:    Long     = expiresIn    @JsonProperty("expires_in")    get
    val scope:        String   = scope        @JsonProperty("scope")         get
    val createdOn:    Instant  = createdOn    @JsonProperty("created_on")    get
    val userId:       String   = userId       @JsonProperty("user_id")       get
    val spotifyUser:  String?  = null         @JsonProperty("spotify_user")  get
    
    val expireTime:   Instant      = createdOn.plusSeconds(expiresIn)

    fun isExpired(now: Instant) = now.isAfter(expireTime) || now == expireTime
}