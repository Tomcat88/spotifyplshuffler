package it.introini.spotifyplshuffler.frontend.views

import it.introini.spotifyplshuffler.frontend.api.ShufflerClient
import kotlinx.html.div
import kotlinx.html.js.onClickFunction
import react.RProps
import react.RState
import react.ReactComponentSpec
import react.ReactElement
import react.dom.ReactDOMBuilder
import react.dom.ReactDOMComponent
import react.materialui.RaisedButton

class LoginViewProps: RProps()
class LoginViewState(var user: String? = null,
                     var password: String? = null): RState
class LoginView: ReactDOMComponent<LoginViewProps, LoginViewState>() {
    companion object: ReactComponentSpec<LoginView, LoginViewProps, LoginViewState>

    init {
        state = LoginViewState()
    }

    override fun ReactDOMBuilder.render() {
        div {
            RaisedButton {
                label = "Login con spotify"
                backgroundColor = "#1ED760"
                onClickFunction = {
                    ShufflerClient.login()
                }
            }
        }
    }
}
