package react.materialui

import org.w3c.dom.events.Event
import react.RProps
import react.ReactComponentNoProps
import react.ReactExternalComponentSpec

private val MaterialUi: dynamic = runtime.wrappers.require("material-ui")
private val MaterialUiStyles: dynamic = runtime.wrappers.require("material-ui/styles")
private val MaterialUiSvgIcons: dynamic = runtime.wrappers.require("material-ui/svg-icons")
private val MaterialUiSvgIconsNavigation: dynamic = runtime.wrappers.require("material-ui/svg-icons/navigation/more-vert")


private val MuiThemeProvider: dynamic = MaterialUiStyles.MuiThemeProvider

object MaterialUiMuiThemeProvider: ReactExternalComponentSpec<ReactComponentNoProps>(MuiThemeProvider)

open class BaseMUIProps(var id: String? = null,
                        var name: String? = null,
                        var className: String? = null): RProps()

class MenuItemProps: BaseMUIProps()
object MenuItem: ReactExternalComponentSpec<MenuItemProps>(MaterialUi.MenuItem)

class IconMenuProps(var iconButtonElement: Any? = null): BaseMUIProps()
object IconMenu: ReactExternalComponentSpec<IconMenuProps>(MaterialUi.IconMenu)

class IconProps(var color: String? = null): BaseMUIProps()
object MoreVertIcon: ReactExternalComponentSpec<IconProps>(MaterialUiSvgIconsNavigation.default)

class ButtonProps(var label: String? = null,
                  var primary: Boolean = false,
                  var secondary: Boolean = false,
                  var backgroundColor: String? = null,
                  var onClick: (Event) -> Unit): BaseMUIProps()
object RaisedButton : ReactExternalComponentSpec<ButtonProps>(MaterialUi.RaisedButton)
object FlatButton   : ReactExternalComponentSpec<ButtonProps>(MaterialUi.FlatButton)

class IconButtonProps(var touch: Boolean = false,
                      var tooltip: String? = null,
                      var tooltipPosition: String? = null ): BaseMUIProps()
object IconButton   : ReactExternalComponentSpec<IconButtonProps>(MaterialUi.IconButton)

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
class  ListItemProps(var leftAvatar: Any? = null,
                     var rightIconButton: Any? = null,
                     var primaryText: String? = null,
                     var secondaryText: String? = null,
                     var onClick: (Event) -> Unit): BaseMUIProps()
object ListItem: ReactExternalComponentSpec<ListItemProps>(MaterialUiList.ListItem)

class AvatarProps(var src: String? = null,
                  var size: Int? = null): BaseMUIProps()
object Avatar: ReactExternalComponentSpec<AvatarProps>(MaterialUi.Avatar)