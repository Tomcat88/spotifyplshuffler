package it.introini.spotifyplshuffler.spotify

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyUser( @JsonProperty("display_name") val displayName: String,
                        @JsonProperty("country")      val country: String,
                        @JsonProperty("birthdate")    val birthDate: String,
                        @JsonProperty("email")        val email: String,
                        @JsonProperty("id")           val id: String,
                        @JsonProperty("product")      val product: String,
                        @JsonProperty("type")         val type: String,
                        @JsonProperty("uri")          val uri: String,
                        @JsonProperty("images")       val spotifyImage: Collection<SpotifyImage> )

data class SpotifyImage( @JsonProperty("height") val height: Int?,
                         @JsonProperty("width")  val width: Int?,
                         @JsonProperty("url")    val url: String )

