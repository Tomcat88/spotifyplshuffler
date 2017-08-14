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
                        var name: String? = null,
                        var className: String? = null): RProps()

class ButtonProps(var label: String? = null,
                  var primary: Boolean = false,
                  var secondary: Boolean = false,
                  var backgroundColor: String? = null,
                  var onClick: (Event) -> Unit): BaseMUIProps()
object RaisedButton : ReactExternalComponentSpec<ButtonProps>(MaterialUi.RaisedButton)
object FlatButton   : ReactExternalComponentSpec<ButtonProps>(MaterialUi.FlatButton)


class TextFieldProps(var floatingLabelText: String? = null,
                     var floatingLabelFixed: Boolean = false,
                     var onChange: (Event, String?) -> Unit): BaseMUIProps()
object TextField: ReactExternalComponentSpec<TextFieldProps>(MaterialUi.TextField)

class AppBarProps(var title: String? = null,
                  var iconElementRight: Any? = null,
                  var iconElementLeft: Any? = null,
                  var showMenuIconButton: Boolean = false,
                  var iconClassNameLeft: String? = "muidocs-icon-navigation-expand-more" ): RProps()
object AppBar: ReactExternalComponentSpec<AppBarProps>(MaterialUi.AppBar)

object Subheader: ReactExternalComponentSpec<RProps>(MaterialUi.Subheader)

private val MaterialUiList: dynamic = runtime.wrappers.require("material-ui/List")
class  ListProps: BaseMUIProps()
object List: ReactExternalComponentSpec<ListProps>(MaterialUiList.List)
class  ListItemProps(var primaryText: String? = null): BaseMUIProps()
object ListItem: ReactExternalComponentSpec<ListItemProps>(MaterialUiList.ListItem)