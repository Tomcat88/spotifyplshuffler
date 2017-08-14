package it.introini.spotifyplshuffler.frontend.views

import it.introini.spotifyplshuffler.frontend.api.ShufflerClient
import it.introini.spotifyplshuffler.frontend.api.SpotifyPlaylist
import kotlinx.coroutines.experimental.launch
import kotlinx.html.div
import kotlinx.html.h4
import react.RProps
import react.RState
import react.ReactComponentSpec
import react.dom.ReactDOMBuilder
import react.dom.ReactDOMComponent
import react.materialui.*


class HomeViewProps(var userId: String,
                    var onLogout: () -> Unit): RProps()
class HomeViewState(var playlists: Collection<SpotifyPlaylist> = emptyList()): RState

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
                        state.playlists.map {
                        ListItem {
                            primaryText = it.name
                        }
                    }
                } else {
                    h4 { +"Nessuna playlist trovata" }
                }
                List {
                    className = "pl-tracks"
                    Subheader { +"Tracks" }
                    ListItem {
                        primaryText = "plt1"
                    }
                    ListItem {
                        primaryText = "plt2"
                    }
                }
            }
        }

    }
}