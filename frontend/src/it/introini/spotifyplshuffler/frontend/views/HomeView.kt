package it.introini.spotifyplshuffler.frontend.views

import it.introini.spotifyplshuffler.frontend.api.ShufflerClient
import it.introini.spotifyplshuffler.frontend.api.SpotifyPlaylist
import it.introini.spotifyplshuffler.frontend.api.SpotifyPlaylistTrack
import kotlinx.coroutines.experimental.launch
import kotlinx.html.div
import kotlinx.html.i
import kotlinx.html.span
import react.RProps
import react.RState
import react.ReactComponentSpec
import react.dom.ReactDOMBuilder
import react.dom.ReactDOMComponent
import react.materialui.*
import runtime.wrappers.msToHMS
import kotlin.js.Date


class HomeViewProps(var userId: String,
                    var onLogout: () -> Unit): RProps()
class HomeViewState(var playlists: Collection<SpotifyPlaylist> = emptyList(),
                    var selectedPlaylist: SpotifyPlaylist? = null,
                    var selectedTracks: Collection<SpotifyPlaylistTrack>? = null): RState

class HomeView: ReactDOMComponent<HomeViewProps, HomeViewState>() {
    companion object: ReactComponentSpec<HomeView, HomeViewProps, HomeViewState>

    init {
        state = HomeViewState()
        launch {
            val playlist = ShufflerClient.getPlaylist(props.userId)
            setState {
                playlists = playlist.items
            }
        }
    }

    private fun onPlSelected(pl: SpotifyPlaylist) {
        setState {
            selectedPlaylist = pl
            selectedTracks = null
        }
        launch {
            val tracks = ShufflerClient.getPlaylistTracks(props.userId, pl.id, pl.owner.id)
            setState {
                selectedTracks = tracks
            }
        }
    }

    override fun ReactDOMBuilder.render() {
        val logoutBt = FlatButton {
            label = "logout"
            onClick = {
                props.onLogout()
            }
        }
        div {
            AppBar {
                title = "Spotify Shuffler"
                iconElementRight = logoutBt
            }
            div("pl-body") {
                if (state.playlists.isNotEmpty()) List {
                    className = "pl-list"
                    children =
                        Subheader { +"Playlists" }
                        state.playlists.map { pl ->
                        ListItem {
                            leftAvatar = pl.spotifyImages.firstOrNull()?.url?.let { Avatar {
                                key = "delay"
                                src = it
                            } } ?: Avatar {
                                key = "delay"
                                children = pl.name[0].toString().toUpperCase()
                            }
                            primaryText = pl.name
                            secondaryText = "${pl.owner.displayName} -- ${pl.tracks.total} songs"
                            onClick = { onPlSelected(pl) }
                        }
                    }
                } else {
                    i { +"Nessuna playlist trovata" }
                }
                if (state.selectedPlaylist != null) List {
                    className = "pl-tracks"
                    Subheader { +"Tracks -- ${state.selectedPlaylist!!.name}" }
                    if (state.selectedTracks != null) {
                        state.selectedTracks!!.map { t ->
                            ListItem {
                                primaryText = t.track.name
                                secondaryText = "${msToHMS(t.track.durationMs)} -- ${t.track.artists.map { it.name }.joinToString(", ")}"
                            }
                        }
                    } else {
                        span { +"Caricando..." }
                    }
                } else {
                    i { +"Seleziona una playlists per vederne le tracce" }
                }
            }
        }

    }
}