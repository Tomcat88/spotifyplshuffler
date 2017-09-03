package it.introini.spotifyplshuffler.frontend.views

import it.introini.spotifyplshuffler.frontend.api.ShufflerClient
import it.introini.spotifyplshuffler.frontend.api.SpotifyCurrentPlayingContext
import it.introini.spotifyplshuffler.frontend.api.SpotifyDevice
import kotlinx.coroutines.experimental.launch
import kotlinx.html.div
import kotlinx.html.span
import react.RProps
import react.RState
import react.ReactComponentSpec
import react.dom.ReactDOMBuilder
import react.dom.ReactDOMComponent
import react.materialui.*


class DevicesViewProps(var userId: String): RProps()
class DevicesViewState(var devices: Collection<SpotifyDevice> = emptyList(),
                       var playback: SpotifyCurrentPlayingContext? = null): RState

class DevicesView: ReactDOMComponent<DevicesViewProps, DevicesViewState>() {
    companion object: ReactComponentSpec<DevicesView, DevicesViewProps, DevicesViewState>
    init {
        state = DevicesViewState()
        launch {
            val shufflerDevices = ShufflerClient.getDevices(props.userId)
            val shufflerPlayback = ShufflerClient.getPlayback(props.userId)
            console.log(shufflerPlayback)
            this.setState {
                devices = shufflerDevices
                playback = shufflerPlayback
            }
        }
    }

    private fun refreshPlayback() {
        launch {
            val pb = ShufflerClient.getPlayback(props.userId)
            this.setState {
                playback = pb
            }
        }

    }

    private fun getToggleLabel(): String? {
        if (state.playback == null) return null
        return if (state.playback!!.isPlaying) "stop" else "start"
    }

    override fun ReactDOMBuilder.render() {
        div("devices-body") {
            div("devices") {
                Subheader { +"Devices" }
                if (state.devices.isEmpty())
                    ListItem { primaryText = "No device found" }
                else
                    state.devices.map {
                        ListItem {
                            primaryText = it.name
                            leftIcon = when(it.type.toLowerCase()) {
                                "computer"   -> ComputerIcon   { key = "delay" }
                                "smartphone" -> SmartphoneIcon { key = "delay" }
                                "speaker"    -> SpeakerIcon    { key = "delay" }
                                else         -> DevicesIcon    { key = "delay" }
                            }
                    }
                }
            }
            div("playback") {
                Subheader { +"Currently playing" }
                if (state.playback != null) {
                    Card {
                        CardHeader {
                            title = state.playback!!.item?.name
                            subtitle = state.playback!!.item?.getArtists()
                        }
                        CardText {
                            +"Playing on ${state.playback!!.device?.name}"
                        }
                        CardActions {
                            FlatButton {
                                label = "refresh"
                                onClick = { refreshPlayback() }
                            }
                            FlatButton {
                                label = getToggleLabel()
                            }
                        }
                    }
                }
            }
        }
    }
}