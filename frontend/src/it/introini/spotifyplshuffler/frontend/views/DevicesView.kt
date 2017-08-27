package it.introini.spotifyplshuffler.frontend.views

import it.introini.spotifyplshuffler.frontend.api.ShufflerClient
import it.introini.spotifyplshuffler.frontend.api.SpotifyDevice
import kotlinx.coroutines.experimental.launch
import kotlinx.html.div
import react.RProps
import react.RState
import react.ReactComponentSpec
import react.dom.ReactDOMBuilder
import react.dom.ReactDOMComponent
import react.materialui.*


class DevicesViewProps(var userId: String): RProps()
class DevicesViewState(var devices: Collection<SpotifyDevice> = emptyList()): RState

class DevicesView: ReactDOMComponent<DevicesViewProps, DevicesViewState>() {
    companion object: ReactComponentSpec<DevicesView, DevicesViewProps, DevicesViewState>
    init {
        state = DevicesViewState()
        launch {
            val shufflerDevices = ShufflerClient.getDevices(props.userId)
            this.setState { devices = shufflerDevices }
        }
    }
    override fun ReactDOMBuilder.render() {
        div {
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
    }
}