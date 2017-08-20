package it.introini.spotifyplshuffler.frontend.views

import it.introini.jscookie.Cookies
import kotlinx.html.div
import react.RState
import react.ReactComponentEmptyProps
import react.ReactComponentSpec
import react.dom.ReactDOM
import react.dom.ReactDOMBuilder
import react.dom.ReactDOMComponent
import react.dom.render
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
        val userId = Cookies.get("userId")
        console.log(userId)
        state = ApplicationState(getFirstView(userId), userId)
    }

    override fun ReactDOMBuilder.render() {
        div("content") {
            when (state.view) {
                MainView.Login -> div {
                    LoginView {}
                }
                MainView.Playlists -> div {
                    ApplicationBar {
                        onLogout = { logout() }
                        switchView = { setState { view = it }}
                    }
                    HomeView {
                        userId = state.userId!!
                    }
                }
                MainView.Devices -> div {
                    ApplicationBar {
                        onLogout = { logout() }
                        switchView = { setState { view = it }}
                    }
                    DevicesView {

                    }
                }
            }
        }
    }

    private fun logout() {
        Cookies.remove("userId")
        setState {
            userId = null
            view = MainView.Login
        }
    }

    private fun getFirstView(userId: String?): MainView {
        return if (userId != null) MainView.Playlists
               else                MainView.Login
    }
}

enum class MainView {
    Login,
    Playlists,
    Devices
}

class ApplicationState(var view: MainView,
                       var userId: String? = null): RState