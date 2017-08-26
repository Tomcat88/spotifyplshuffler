package it.introini.spotifyplshuffler.frontend.api

data class SpotifyUser(
    val displayName: String?,
    val country: String,
    val birthDate: String,
    val email: String,
    val id: String,
    val product: String,
    val type: String,
    val uri: String,
    val spotifyImage: Collection<SpotifyImage>
)

data class SpotifyPublicUser(
    val displayName: String?,
    val id: String,
    val type: String,
    val uri: String,
    val spotifyImages: Collection<SpotifyImage>?
)

data class SpotifyImage(
    val height: Int?,
    val width: Int?,
    val url: String
)

data class PagingObject<out T> (
    val items: List<T>,
    val href: String,
    val limit: Int,
    val next: String?,
    val offset: Int,
    val previous: String?,
    val total: Int
)

data class SpotifyPlaylist (
    val collaborative: Boolean,
    val description: String?,
    val owner: SpotifyPublicUser,
    val href: String,
    val id: String,
    val spotifyImages: Collection<SpotifyImage>,
    val name: String,
    val public: Boolean?,
    val snapshotId: String,
    val type: String,
    val uri: String,
    val tracks: TracksObject
)

data class TracksObject (
    val href: String,
    val total: Int
)

data class SpotifyPlaylistTrack (
    val addedAt: Long,
    val isLocal: Boolean,
    val track: SpotifyTrack
)

data class SpotifyTrack (
    val id: String?,
    val name: String,
    val artists: Collection<SpotifyArtist>,
    val availableMarkets: Collection<String>,
    val discNumber: Int,
    val durationMs: Int,
    val explicit: Boolean,
    val href: String?,
    val isPlayable: Boolean,
    val popularity: Int,
    val previewUrl: String?,
    val trackNumber: Int,
    val type: String,
    val uri: String
)

data class SpotifyArtist (
    val id: String?,
    val href: String?,
    val name: String?,
    val type: String?,
    val uri: String?
)

data class SpotifyDevice (
    val id: String?,
    val isActive: Boolean,
    val isRestricted: Boolean,
    val name: String,
    val type: String,
    val volumePercent: Int?
)
