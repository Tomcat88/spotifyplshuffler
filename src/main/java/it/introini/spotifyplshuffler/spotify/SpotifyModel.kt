package it.introini.spotifyplshuffler.spotify

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.core.json.JsonObject
import java.time.Instant
import java.time.LocalDate

interface Encodable {
    fun toJson(): JsonObject = JsonObject.mapFrom(this)
    fun encode(): String = JsonObject.mapFrom(this).encode()
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyUser( @JsonProperty("display_name") val displayName: String,
                        @JsonProperty("country")      val country: String,
                        @JsonProperty("birthdate")    val birthDate: LocalDate,
                        @JsonProperty("email")        val email: String,
                        @JsonProperty("id")           val id: String,
                        @JsonProperty("product")      val product: String,
                        @JsonProperty("type")         val type: String,
                        @JsonProperty("uri")          val uri: String,
                        @JsonProperty("images")       val spotifyImage: Collection<SpotifyImage> ) : Encodable

data class SpotifyImage( @JsonProperty("height") val height: Int?,
                         @JsonProperty("width")  val width: Int?,
                         @JsonProperty("url")    val url: String ) : Encodable

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyPlaylist (
    @JsonProperty("collaborative") val collaborative: Boolean,
    @JsonProperty("description")   val description: String?,
    @JsonProperty("href")          val href: String,
    @JsonProperty("id")            val id: String,
    @JsonProperty("images")        val spotifyImages: Collection<SpotifyImage>,
    @JsonProperty("name")          val name: String,
    @JsonProperty("public")        val public: Boolean?,
    @JsonProperty("snapshot_id")   val snapshotId: String,
    @JsonProperty("type")          val type: String,
    @JsonProperty("uri")           val uri: String,
    @JsonProperty("tracks")        val tracks: TracksObject
): Encodable

data class TracksObject (
    @JsonProperty("href")  val href: String,
    @JsonProperty("total") val total: Int
): Encodable

data class PagingObject<out T> (
    @JsonProperty("items")    val items: List<T>,
    @JsonProperty("href")     val href: String,
    @JsonProperty("limit")    val limit: Int,
    @JsonProperty("next")     val next: String,
    @JsonProperty("offset")   val offset: Int,
    @JsonProperty("previous") val previous: Int,
    @JsonProperty("total")    val total: Int
): Encodable

data@JsonIgnoreProperties(ignoreUnknown = true)
class SpotifyPlaylistTrack (
    @JsonProperty("added_at") val addedAt: Instant,
    @JsonProperty("is_local") val isLocal: Boolean,
    @JsonProperty("track")    val track: SpotifyTrack
): Encodable

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyTrack (
    @JsonProperty("id")                val id: String,
    @JsonProperty("name")              val name: String,
    @JsonProperty("artists")           val artists: Collection<SpotifyArtist>,
    @JsonProperty("available_markets") val availableMarkets: Collection<String>,
    @JsonProperty("disc_number")       val discNumber: Int,
    @JsonProperty("duration_ms")       val durationMs: Int,
    @JsonProperty("explicit")          val explicit: Boolean,
    @JsonProperty("href")              val href: String,
    @JsonProperty("is_playable")       val isPlayable: Boolean,
    @JsonProperty("popularity")        val popularity: Int,
    @JsonProperty("preview_url")       val previewUrl: String?,
    @JsonProperty("track_number")      val trackNumber: Int,
    @JsonProperty("type")              val type: String,
    @JsonProperty("uri")               val uri: String
): Encodable

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyArtist (
    @JsonProperty("id")   val id: String,
    @JsonProperty("href") val href: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("type") val type: String,
    @JsonProperty("uri")  val uri: String
): Encodable

// ==== ERRORS ====

data class SpotifyApiException( @JsonProperty("error") val error: RegularErrorObject): RuntimeException()

data class RegularErrorObject( @JsonProperty("status")  val status: Int,
                               @JsonProperty("message") val message: String)

data class SpotifyAuthException( @JsonProperty("error")             val error: String,
                                 @JsonProperty("error_description") val errorDescription: String): RuntimeException()
