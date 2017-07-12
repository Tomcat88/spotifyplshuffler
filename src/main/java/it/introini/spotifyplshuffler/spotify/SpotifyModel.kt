package it.introini.spotifyplshuffler.spotify

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyUser( @JsonProperty("display_name") val displayName: String,
                        @JsonProperty("country")      val country: String,
                        @JsonProperty("birthdate")    val birthDate: LocalDate,
                        @JsonProperty("email")        val email: String,
                        @JsonProperty("id")           val id: String,
                        @JsonProperty("product")      val product: String,
                        @JsonProperty("type")         val type: String,
                        @JsonProperty("uri")          val uri: String,
                        @JsonProperty("images")       val spotifyImage: Collection<SpotifyImage> )

data class SpotifyImage( @JsonProperty("height") val height: Int?,
                         @JsonProperty("width")  val width: Int?,
                         @JsonProperty("url")    val url: String )

data class SpotifyPlaylist (
    @JsonProperty("collaborative") val collaborative: Boolean,
    @JsonProperty("description")   val description: String,
    @JsonProperty("href")          val href: String,
    @JsonProperty("id")            val id: String,
    @JsonProperty("images")        val spotifyImages: Collection<SpotifyImage>,
    @JsonProperty("name")          val name: String,
    @JsonProperty("public")        val public: Boolean?,
    @JsonProperty("snapshot_id")   val snapshotId: String,
    @JsonProperty("type")          val type: String,
    @JsonProperty("uri")           val uri: String,
    @JsonProperty("tracks")        val tracks: TracksObject
)

data class TracksObject (
    @JsonProperty("href")  val href: String,
    @JsonProperty("total") val total: Int
)

data class PagingObject<out T> (
    @JsonProperty("items")    val items: Collection<T>,
    @JsonProperty("href")     val href: String,
    @JsonProperty("limit")    val limit: Int,
    @JsonProperty("next")     val next: String,
    @JsonProperty("offset")   val offset: Int,
    @JsonProperty("previous") val previous: Int,
    @JsonProperty("total")    val total: Int
)

data class SpotifyPlaylistTrack (
        @JsonProperty("added_at") val addedAt: Instant,
        @JsonProperty("track")    val track: SpotifyTrack
)

data class SpotifyTrack (
        val id: String
)