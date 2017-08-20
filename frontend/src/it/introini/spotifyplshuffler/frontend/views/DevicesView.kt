package it.introini.spotifyplshuffler.frontend.views

import kotlinx.html.div
import kotlinx.html.i
import react.RProps
import react.RState
import react.ReactComponentSpec
import react.dom.ReactDOMBuilder
import react.dom.ReactDOMComponent


class DevicesViewProps: RProps()
class DevicesViewState: RState

class DevicesView: ReactDOMComponent<DevicesViewProps, DevicesViewState>() {
    companion object: ReactComponentSpec<DevicesView, DevicesViewProps, DevicesViewState>
    init {
        state = DevicesViewState()
    }
    override fun ReactDOMBuilder.render() {
        div {
            i { +"Devices" }
        }
    }
}