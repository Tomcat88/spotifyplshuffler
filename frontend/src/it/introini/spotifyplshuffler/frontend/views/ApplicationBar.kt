package it.introini.spotifyplshuffler.frontend.views

import kotlinx.html.div
import kotlinx.html.js.onClickFunction
import react.RProps
import react.RState
import react.ReactComponentSpec
import react.dom.ReactDOMBuilder
import react.dom.ReactDOMComponent
import react.materialui.*


class ApplicationBarProps(var onLogout: () -> Unit,
                          var switchView: (view: MainView) -> Unit): RProps()
class ApplicationBarState(var drawerOpen: Boolean = false): RState

class ApplicationBar: ReactDOMComponent<ApplicationBarProps, ApplicationBarState>() {
    companion object: ReactComponentSpec<ApplicationBar, ApplicationBarProps, ApplicationBarState>

    init {
        state = ApplicationBarState()
    }

    private fun toggleDrawer() {
        setState {
            drawerOpen = !state.drawerOpen
        }
    }

    private fun onMenuItemClick(view: MainView) {
        toggleDrawer()
        props.switchView(view)
    }

    override fun ReactDOMBuilder.render() {
        div {
            AppBar {
                title = "Spotify Shuffler"
                showMenuIconButton = true
                iconElementLeft = IconButton {
                    key = "delay"
                    onClickFunction = { toggleDrawer() }
                    MenuIcon {}
                }
                iconElementRight = FlatButton {
                    key = "delay"
                    label = "logout"
                    onClick = {
                        props.onLogout()
                    }
                }
            }
            Drawer {
                open = state.drawerOpen
                AppBar{
                    title = "Menu"
                    iconElementRight = IconButton {
                        key = "delay"
                        onClickFunction = {
                            toggleDrawer()
                        }
                        CloseIcon {}
                    }
                }
                MenuItem {
                    leftIcon = LibraryMusicIcon {
                        key = "delay"
                    }
                    onClick = {
                        onMenuItemClick(MainView.Playlists)
                    }
                    +"Playlists"

                }
                MenuItem {
                    leftIcon = DevicesIcon {
                        key = "delay"
                    }
                    onClick = {
                        onMenuItemClick(MainView.Devices)
                    }
                    +"Devices"
                }
            }
        }
    }
}