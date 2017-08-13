package it.introini.spotifyplshuffler.frontend.views

import kotlinx.html.div
import react.RProps
import react.RState
import react.ReactComponentSpec
import react.dom.ReactDOMBuilder
import react.dom.ReactDOMComponent


class HomeViewProps(var userId: String): RProps()
class HomeViewState: RState

class HomeView: ReactDOMComponent<HomeViewProps, HomeViewState>() {
    companion object: ReactComponentSpec<HomeView, HomeViewProps, HomeViewState>

    init {
        state = HomeViewState()
    }

    override fun ReactDOMBuilder.render() {
        div { +"Logged as ${props.userId}" }
    }
}