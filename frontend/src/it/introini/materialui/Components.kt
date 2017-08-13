package react.materialui

import org.w3c.dom.events.Event
import react.RProps
import react.ReactComponentNoProps
import react.ReactExternalComponentSpec

private val MaterialUi: dynamic = runtime.wrappers.require("material-ui")
private val MaterialUiStyles: dynamic = runtime.wrappers.require("material-ui/styles")

private val MuiThemeProvider: dynamic = MaterialUiStyles.MuiThemeProvider

object MaterialUiMuiThemeProvider: ReactExternalComponentSpec<ReactComponentNoProps>(MuiThemeProvider)

open class BaseMUIProps(var id: String? = null,
                        var name: String? = null): RProps()

class RaisedButtonProps(var label: String? = null,
                        var primary: Boolean = false,
                        var secondary: Boolean = false,
                        var backgroundColor: String? = null): RProps()
object RaisedButton : ReactExternalComponentSpec<RaisedButtonProps>(MaterialUi.RaisedButton)


class TextFieldProps(var floatingLabelText: String? = null,
                     var floatingLabelFixed: Boolean = false,
                     var onChange: (Event, String?) -> Unit): BaseMUIProps()
object TextField: ReactExternalComponentSpec<TextFieldProps>(MaterialUi.TextField)