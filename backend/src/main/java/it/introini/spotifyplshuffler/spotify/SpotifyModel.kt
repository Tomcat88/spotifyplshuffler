package it.introini.spotifyplshuffler.spotify

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.json.JsonObject
import java.time.Instant
import java.time.LocalDate

interface Encodable {
    fun toJson(): JsonObject = JsonObject.mapFrom(this)
    fun encode(): String = JsonObject.mapFrom(this).encode()
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyUser( @JsonProperty("display_name") val displayName: String?,
                        @JsonProperty("country")      val country: String,
                        @JsonProperty("birthdate")    val birthDate: LocalDate,
                        @JsonProperty("email")        val email: String,
                        @JsonProperty("id")           val id: String,
                        @JsonProperty("product")      val product: String,
                        @JsonProperty("type")         val type: String,
                        @JsonProperty("uri")          val uri: String,
                        @JsonProperty("images")       val spotifyImage: Collection<SpotifyImage> ) : Encodable

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyPublicUser(@JsonProperty("display_name") val displayName: String?,
                             @JsonProperty("id")           val id: String,
                             @JsonProperty("type")         val type: String,
                             @JsonProperty("uri")          val uri: String,
                             @JsonProperty("images")       val spotifyImages: Collection<SpotifyImage>? ) : Encodable

data class SpotifyImage( @JsonProperty("height") val height: Int?,
                         @JsonProperty("width")  val width: Int?,
                         @JsonProperty("url")    val url: String ) : Encodable

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyPlaylist (
    @JsonProperty("collaborative") val collaborative: Boolean,
    @JsonProperty("description")   val description: String?,
    @JsonProperty("owner")         val owner: SpotifyPublicUser,
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

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyPlaylistFull (
    @JsonProperty("collaborative") val collaborative: Boolean,
    @JsonProperty("description")   val description: String?,
    @JsonProperty("owner")         val owner: SpotifyPublicUser,
    @JsonProperty("href")          val href: String,
    @JsonProperty("id")            val id: String,
    @JsonProperty("images")        val spotifyImages: Collection<SpotifyImage>,
    @JsonProperty("name")          val name: String,
    @JsonProperty("public")        val public: Boolean?,
    @JsonProperty("snapshot_id")   val snapshotId: String,
    @JsonProperty("type")          val type: String,
    @JsonProperty("uri")           val uri: String,
    @JsonProperty("tracks")        val tracks: PagingObject<SpotifyPlaylistTrack>
): Encodable

@JsonIgnoreProperties(ignoreUnknown = true)
class PlaylistFull (
    @JsonProperty("collaborative") collaborative: Boolean,
    @JsonProperty("description")   description: String?,
    @JsonProperty("owner")         owner: SpotifyPublicUser,
    @JsonProperty("href")          href: String,
    @JsonProperty("id")            id: String,
    @JsonProperty("images")        spotifyImages: Collection<SpotifyImage>,
    @JsonProperty("name")          name: String,
    @JsonProperty("public")        public: Boolean?,
    @JsonProperty("snapshot_id")   snapshotId: String,
    @JsonProperty("type")          type: String,
    @JsonProperty("uri")           uri: String,
    @JsonProperty("tracks")        tracks: Collection<SpotifyPlaylistTrack>
) {

    val collaborative: Boolean                   = collaborative @JsonProperty("collaborative") get
    val description: String?                     = description   @JsonProperty("description")   get
    val owner: SpotifyPublicUser                 = owner         @JsonProperty("owner")         get
    val href: String                             = href          @JsonProperty("href")          get
    val id: String                               = id            @JsonProperty("id")            get
    val spotifyImages: Collection<SpotifyImage>  = spotifyImages @JsonProperty("images")        get
    val name: String                             = name          @JsonProperty("name")          get
    val public: Boolean?                         = public        @JsonProperty("public")        get
    val snapshotId: String                       = snapshotId    @JsonProperty("snapshot_id")   get
    val type: String                             = type          @JsonProperty("type")          get
    val uri: String                              = uri           @JsonProperty("uri")           get
    val tracks: Collection<SpotifyPlaylistTrack> = tracks        @JsonProperty("tracks")        get

}

data class TracksObject (
    @JsonProperty("href")  val href: String,
    @JsonProperty("total") val total: Int
): Encodable

data class PagingObject<out T> (
    @JsonProperty("items")    val items: List<T>,
    @JsonProperty("href")     val href: String,
    @JsonProperty("limit")    val limit: Int,
    @JsonProperty("next")     val next: String?,
    @JsonProperty("offset")   val offset: Int,
    @JsonProperty("previous") val previous: String?,
    @JsonProperty("total")    val total: Int
): Encodable

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyPlaylistTrack (
    @JsonProperty("added_at") val addedAt: Instant,
    @JsonProperty("is_local") val isLocal: Boolean,
    @JsonProperty("track")    val track: SpotifyTrack
): Encodable

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyTrack (
        @JsonProperty("id")                val id: String?,
        @JsonProperty("name")              val name: String,
        @JsonProperty("artists")           val artists: Collection<SpotifyArtist>,
        @JsonProperty("album")             val album: SpotifyAlbum?,
        @JsonProperty("available_markets") val availableMarkets: Collection<String>,
        @JsonProperty("disc_number")       val discNumber: Int,
        @JsonProperty("duration_ms")       val durationMs: Int,
        @JsonProperty("explicit")          val explicit: Boolean,
        @JsonProperty("href")              val href: String?,
        @JsonProperty("is_playable")       val isPlayable: Boolean,
        @JsonProperty("popularity")        val popularity: Int,
        @JsonProperty("preview_url")       val previewUrl: String?,
        @JsonProperty("track_number")      val trackNumber: Int,
        @JsonProperty("type")              val type: String,
        @JsonProperty("uri")               val uri: String
): Encodable

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyAlbum (
    @JsonProperty("album_type")        val albumType: String,
    @JsonProperty("artists")           val artists: Collection<SpotifyArtist>,
    @JsonProperty("available_markets") val availableMarkets: Collection<String>,
    @JsonProperty("href")              val href: String?,
    @JsonProperty("id")                val id: String?,
    @JsonProperty("images")            val spotifyImages: Collection<SpotifyImage>,
    @JsonProperty("name")              val name: String?,
    @JsonProperty("type")              val type: String,
    @JsonProperty("uri")               val uri: String
): Encodable

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyArtist (
    @JsonProperty("id")   val id: String?,
    @JsonProperty("href") val href: String?,
    @JsonProperty("name") val name: String?,
    @JsonProperty("type") val type: String?,
    @JsonProperty("uri")  val uri: String?
): Encodable

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyDevices (
        @JsonProperty("devices") val devices: Collection<SpotifyDevice>
): Encodable

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyDevice (
    @JsonProperty("id")             val id: String?,
    @JsonProperty("is_active")      val isActive: Boolean,
    @JsonProperty("is_restricted")  val isRestricted: Boolean,
    @JsonProperty("name")           val name: String,
    @JsonProperty("type")           val type: String,
    @JsonProperty("volume_percent") val volumePercent: Int?
): Encodable

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyCurrentPlayingContext (
        @JsonProperty("device")        val device: SpotifyDevice?,
        @JsonProperty("repeat_state")  val repeatState: String,
        @JsonProperty("shuffle_state") val shuffleState: Boolean,
        @JsonProperty("context")       val context: SpotifyContextObject?,
        @JsonProperty("timestamp")     val timestamp: Long,
        @JsonProperty("progress_ms")   val progressMs: Long?,
        @JsonProperty("is_playing")    val isPlaying: Boolean,
        @JsonProperty("item")          val item: SpotifyTrack?
): Encodable

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyContextObject (
        @JsonProperty("uri")  val uri: String,
        @JsonProperty("href") val href: String?,
        @JsonProperty("type") val type: String
): Encodable

// ==== ERRORS ====

data class SpotifyApiException( @JsonProperty("error") val error: RegularErrorObject): RuntimeException()

data class RegularErrorObject( @JsonProperty("status")  val status: Int,
                               @JsonProperty("message") val message: String)

data class SpotifyAuthException( @JsonProperty("error")             val error: String,
                                 @JsonProperty("error_description") val errorDescription: String): RuntimeException()


val INTERNAL_ERROR = SpotifyApiException(RegularErrorObject(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "server error"))