package it.introini.spotifyplshuffler.frontend

import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.js.onClickFunction
import react.RState
import react.ReactComponentEmptyProps
import react.ReactComponentSpec
import react.dom.ReactDOM
import react.dom.ReactDOMBuilder
import react.dom.ReactDOMComponent
import react.dom.render
import react.materialui.MaterialUiButton
import react.materialui.MaterialUiMuiThemeProvider
import kotlin.browser.document


fun main(args: Array<String>) {
    runtime.wrappers.require("pl-shuffler.css")
    val injectTapEventPlugin = runtime.wrappers.require("react-tap-event-plugin")
    injectTapEventPlugin()
    ReactDOM.render(document.getElementById("app")) {
        div {
            MaterialUiMuiThemeProvider {
                Application {}
            }
        }
    }
}

class Application: ReactDOMComponent<ReactComponentEmptyProps, ApplicationState>() {
    companion object : ReactComponentSpec<Application, ReactComponentEmptyProps, ApplicationState>

    init {
        state = ApplicationState(MainView.Login)
    }

    override fun ReactDOMBuilder.render() {
        div("content") {
            MaterialUiButton {
                primary = true
                children = "Ciao"
                onClickFunction = {
                    setState {
                        text = "Thomas"
                    }
                }
            }
            when (state.view) {
                MainView.Login -> h1 { +"Ciao a ${state.text}" }
            }
        }
    }
}

enum class MainView {
    Login

}

class ApplicationState(val view: MainView, var text: String = "Nessuno"): RState