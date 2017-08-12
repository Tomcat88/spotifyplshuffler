package react.materialui

import react.RProps
import react.ReactComponentNoProps
import react.ReactExternalComponentSpec

private val MaterialUi: dynamic = runtime.wrappers.require("material-ui")
private val MaterialUiStyles: dynamic = runtime.wrappers.require("material-ui/styles")

private val MuiThemeProvider: dynamic = MaterialUiStyles.MuiThemeProvider
private val RaisedButton: dynamic = MaterialUi.RaisedButton

object MaterialUiMuiThemeProvider: ReactExternalComponentSpec<ReactComponentNoProps>(MuiThemeProvider)

class ButtonProps(var primary: Boolean = false,
                  var secondary: Boolean = false): RProps()
object MaterialUiButton: ReactExternalComponentSpec<ButtonProps>(RaisedButton)