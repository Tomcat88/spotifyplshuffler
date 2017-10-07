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
import kotlin.browser.window


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
        val path = window.location.pathname.removePrefix("/shuffler")
        val view = getFirstView(userId, path)
        changeUrl(view)
        state = ApplicationState(view, userId)
    }

    private fun changeUrl(view: View) {
        window.history.pushState(null, "Shuffler", "/shuffler${view.path}")
    }

    private fun onSwitchView(newView: View) {
        this.setState {
            view = newView
        }
        changeUrl(newView)
    }

    override fun ReactDOMBuilder.render() {
        div("content") {
            when (state.view) {
                View.Login -> div {
                    LoginView {}
                }
                View.Playlists -> div {
                    ApplicationBar {
                        onLogout = { logout() }
                        switchView = { onSwitchView(it) }
                    }
                    HomeView {
                        userId = state.userId!!
                    }
                }
                View.Devices -> div {
                    ApplicationBar {
                        onLogout = { logout() }
                        switchView = { onSwitchView(it) }
                    }
                    DevicesView {
                        userId = state.userId!!
                    }
                }
            }
        }
    }

    private fun logout() {
        Cookies.remove("userId")
        setState {
            userId = null
            view = View.Login
        }
    }

    private fun getFirstView(userId: String?, path: String): View {
        return if (userId != null) View.parse(path) ?: View.Playlists
               else                View.Login
    }
}

enum class View(val path: String) {
    Login("/"),
    Playlists("/playlists"),
    Devices("/devices");

    companion object {
        fun parse(path: String): View? = View.values().find { it.path == path }
    }
}

class ApplicationState(var view: View,
                       var userId: String? = null): RState