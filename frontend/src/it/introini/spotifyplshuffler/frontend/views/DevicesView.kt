package it.introini.spotifyplshuffler.frontend.views

import it.introini.spotifyplshuffler.frontend.api.PlaybackStatus
import it.introini.spotifyplshuffler.frontend.api.ShufflerClient
import it.introini.spotifyplshuffler.frontend.api.SpotifyCurrentPlayingContext
import it.introini.spotifyplshuffler.frontend.api.SpotifyDevice
import kotlinx.coroutines.experimental.launch
import kotlinx.html.div
import react.RProps
import react.RState
import react.ReactComponentSpec
import react.dom.ReactDOMBuilder
import react.dom.ReactDOMComponent
import react.materialui.*
import kotlin.browser.window


class DevicesViewProps(var userId: String): RProps()
class DevicesViewState(var devices: Collection<SpotifyDevice> = emptyList(),
                       var playback: SpotifyCurrentPlayingContext? = null,
                       var timeoutId: Int? = null): RState

class DevicesView: ReactDOMComponent<DevicesViewProps, DevicesViewState>() {
    companion object: ReactComponentSpec<DevicesView, DevicesViewProps, DevicesViewState>

    private val PROGRESS:Int = 1000

    init {
        state = DevicesViewState()
        refresh()
    }

    private fun refresh() {
        if (state.timeoutId != null) {
            window.clearInterval(state.timeoutId!!)
        }
        launch {
            val shufflerDevices = ShufflerClient.getDevices(props.userId)
            val pb = ShufflerClient.getPlayback(props.userId)
            val toId = if (pb.progressMs != null && pb.isPlaying) {
                window.setInterval({
                    val progress = state.playback?.progressMs ?: 0
                    val max = state.playback?.item?.durationMs ?: 0
                    if (progress + PROGRESS >= max) {
                        refresh()
                    } else {
                        if (state.playback != null) {
                            this.setState {
                                playback = SpotifyCurrentPlayingContext(
                                        state.playback!!.device,
                                        state.playback!!.repeatState,
                                        state.playback!!.shuffleState,
                                        state.playback!!.context,
                                        state.playback!!.timestamp,
                                        progress + PROGRESS,
                                        state.playback!!.isPlaying,
                                        state.playback!!.item
                                )
                            }
                        }
                    }
                }, PROGRESS)
            } else {
                null
            }
            this.setState {
                devices = shufflerDevices
                playback = pb
                timeoutId = toId
            }
        }

    }

    private fun getPlaybackStatus(): PlaybackStatus? {
        if (state.playback == null) return null
        return state.playback!!.getPlaybackStatus()
    }

    private fun stopPlayback() {
        launch {
            ShufflerClient.playbackControl(props.userId, "stop")
            window.setTimeout({ refresh() }, 1500)
        }
    }

    private fun startPlayback() {
        launch {
            ShufflerClient.playbackControl(props.userId, "start")
            window.setTimeout({ refresh() }, 1500)
        }
    }


    private fun skipToNext() {
        launch {
            ShufflerClient.playbackControl(props.userId, "next")
            window.setTimeout({ refresh() }, 1500)
        }
    }

    private fun skipToPrev() {
        launch {
            ShufflerClient.playbackControl(props.userId, "prev")
            window.setTimeout({ refresh() }, 1500)
        }
    }

    private fun shufflePlayback() {
        launch {
            ShufflerClient.shufflePlayback(props.userId, state.playback?.device?.id, !(isPlaybackShuffle()))
            window.setTimeout({ refresh() }, 1500)
        }
    }

    private fun isPlaybackShuffle(): Boolean {
        return state.playback?.shuffleState ?: false
    }



    override fun ReactDOMBuilder.render() {
        div("devices-body") {
            div("devices") {
                Subheader {
                    +"Devices"
                    IconButton {
                        tooltip = "refresh"
                        onClick = { refresh() }
                        RefreshIcon {}
                    }
                }
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
                        div("progressSlider") {
                            if (state.playback!!.progressMs != null && state.playback!!.item?.durationMs != null) {
                                Slider {
                                    min = 0
                                    max = state.playback!!.item?.durationMs!!
                                    value = state.playback!!.progressMs!!
                                }
                            }
                        }
                        CardActions {
                            IconButton {
                                tooltip = "previous"
                                onClick = { skipToPrev() }
                                SkipPrevIcon {}
                            }
                            when (getPlaybackStatus()) {
                                PlaybackStatus.PAUSED -> IconButton {
                                    tooltip = "play"
                                    onClick = { startPlayback() }
                                    PlayIcon {}
                                }
                                PlaybackStatus.PLAYING -> IconButton {
                                    tooltip = "pause"
                                    onClick = { stopPlayback() }
                                    PauseIcon {}
                                }
                                else -> null
                            }
                            IconButton {
                                tooltip = "next"
                                onClick = { skipToNext() }
                                SkipNextIcon {}
                            }
                            IconButton {
                                tooltip = "shuffle"
                                onClick = { shufflePlayback() }
                                ShuffleIcon { color = if (isPlaybackShuffle()) "green" else null }
                            }
                        }
                    }
                }
            }
        }
    }
}