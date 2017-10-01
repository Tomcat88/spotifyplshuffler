package react.materialui

import org.w3c.dom.events.Event
import react.RProps
import react.ReactComponentNoProps
import react.ReactExternalComponentSpec

private val MaterialUi: dynamic = runtime.wrappers.require("material-ui")
private val MaterialUiStyles: dynamic = runtime.wrappers.require("material-ui/styles")
private val MaterialUiSvgIcons: dynamic = runtime.wrappers.require("material-ui/svg-icons")
private val MaterialUiSvgIconsNavigationMoreVert: dynamic = runtime.wrappers.require("material-ui/svg-icons/navigation/more-vert")
private val MaterialUiSvgIconsNavigationMenu: dynamic = runtime.wrappers.require("material-ui/svg-icons/navigation/menu")
private val MaterialUiSvgIconsNavigationClose: dynamic = runtime.wrappers.require("material-ui/svg-icons/navigation/close")
private val MaterialUiSvgIconsAVLibraryMusic: dynamic = runtime.wrappers.require("material-ui/svg-icons/av/library-music")
private val MaterialUiSvgIconsDeviceDevices: dynamic = runtime.wrappers.require("material-ui/svg-icons/device/devices")
private val MaterialUiCard: dynamic = runtime.wrappers.require("material-ui/Card")


private val MuiThemeProvider: dynamic = MaterialUiStyles.MuiThemeProvider

object MaterialUiMuiThemeProvider: ReactExternalComponentSpec<ReactComponentNoProps>(MuiThemeProvider)

open class BaseMUIProps(var id: String? = null,
                        var name: String? = null,
                        var className: String? = null,
                        var style: Any? = null): RProps()

class MenuItemProps(var leftIcon: Any? = null,
                    var rightIcon: Any? = null,
                    var onClick: (Event) -> Unit): BaseMUIProps()
object MenuItem: ReactExternalComponentSpec<MenuItemProps>(MaterialUi.MenuItem)

class IconMenuProps(var iconButtonElement: Any? = null): BaseMUIProps()
object IconMenu: ReactExternalComponentSpec<IconMenuProps>(MaterialUi.IconMenu)

class IconProps(var color: String? = null): BaseMUIProps()
object MoreVertIcon: ReactExternalComponentSpec<IconProps>(MaterialUiSvgIconsNavigationMoreVert.default)
object MenuIcon: ReactExternalComponentSpec<IconProps>(MaterialUiSvgIconsNavigationMenu.default)
object CloseIcon: ReactExternalComponentSpec<IconProps>(MaterialUiSvgIconsNavigationClose.default)
object LibraryMusicIcon: ReactExternalComponentSpec<IconProps>(MaterialUiSvgIconsAVLibraryMusic.default)
object DevicesIcon: ReactExternalComponentSpec<IconProps>(MaterialUiSvgIconsDeviceDevices.default)
object ComputerIcon: ReactExternalComponentSpec<IconProps>(runtime.wrappers.require("material-ui/svg-icons/hardware/computer").default)
object SmartphoneIcon: ReactExternalComponentSpec<IconProps>(runtime.wrappers.require("material-ui/svg-icons/hardware/smartphone").default)
object SpeakerIcon: ReactExternalComponentSpec<IconProps>(runtime.wrappers.require("material-ui/svg-icons/hardware/speaker").default)
object RefreshIcon: ReactExternalComponentSpec<IconProps>(runtime.wrappers.require("material-ui/svg-icons/navigation/refresh").default)
object PauseIcon: ReactExternalComponentSpec<IconProps>(runtime.wrappers.require("material-ui/svg-icons/av/pause").default)
object PlayIcon: ReactExternalComponentSpec<IconProps>(runtime.wrappers.require("material-ui/svg-icons/av/play-arrow").default)
object SkipPrevIcon: ReactExternalComponentSpec<IconProps>(runtime.wrappers.require("material-ui/svg-icons/av/skip-previous").default)
object SkipNextIcon: ReactExternalComponentSpec<IconProps>(runtime.wrappers.require("material-ui/svg-icons/av/skip-next").default)
object ShuffleIcon: ReactExternalComponentSpec<IconProps>(runtime.wrappers.require("material-ui/svg-icons/av/shuffle").default)

class ButtonProps(var label: String? = null,
                  var primary: Boolean = false,
                  var secondary: Boolean = false,
                  var backgroundColor: String? = null,
                  var onClick: (Event) -> Unit): BaseMUIProps()
object RaisedButton : ReactExternalComponentSpec<ButtonProps>(MaterialUi.RaisedButton)
object FlatButton   : ReactExternalComponentSpec<ButtonProps>(MaterialUi.FlatButton)

class IconButtonProps(var touch: Boolean = false,
                      var tooltip: String? = null,
                      var tooltipPosition: String? = null,
                      var onClick: (Event) -> Unit,
                      var iconStyle: Any? = null,
                      var iconClassName: String? = null): BaseMUIProps()
object IconButton   : ReactExternalComponentSpec<IconButtonProps>(MaterialUi.IconButton)

class TextFieldProps(var floatingLabelText: String? = null,
                     var floatingLabelFixed: Boolean = false,
                     var onChange: (Event, String?) -> Unit): BaseMUIProps()
object TextField: ReactExternalComponentSpec<TextFieldProps>(MaterialUi.TextField)

class AppBarProps(var title: String? = null,
                  var iconElementRight: Any? = null,
                  var iconElementLeft: Any? = null,
                  var onLeftIconButtonTouchTap: (Event) -> Unit,
                  var showMenuIconButton: Boolean = false,
                  var iconClassNameLeft: String? = null ): RProps()
object AppBar: ReactExternalComponentSpec<AppBarProps>(MaterialUi.AppBar)

class DrawerProps(var open: Boolean = false): RProps()
object Drawer: ReactExternalComponentSpec<DrawerProps>(MaterialUi.Drawer)

object Subheader: ReactExternalComponentSpec<RProps>(MaterialUi.Subheader)

private val MaterialUiList: dynamic = runtime.wrappers.require("material-ui/List")
class  ListProps: BaseMUIProps()
object List: ReactExternalComponentSpec<ListProps>(MaterialUiList.List)
class  ListItemProps(var leftIcon: Any? = null,
                     var rightIcon: Any? = null,
                     var leftAvatar: Any? = null,
                     var rightIconButton: Any? = null,
                     var primaryText: String? = null,
                     var secondaryText: String? = null,
                     var onClick: (Event) -> Unit): BaseMUIProps()
object ListItem: ReactExternalComponentSpec<ListItemProps>(MaterialUiList.ListItem)

class AvatarProps(var src: String? = null,
                  var size: Int? = null): BaseMUIProps()
object Avatar: ReactExternalComponentSpec<AvatarProps>(MaterialUi.Avatar)

class CardProps: BaseMUIProps()
object Card: ReactExternalComponentSpec<CardProps>(MaterialUiCard.Card)
class CardHeaderProps(var title: String? = null,
                      var subtitle: String? = null,
                      var avatar: Any? = null): BaseMUIProps()
object CardHeader: ReactExternalComponentSpec<CardHeaderProps>(MaterialUiCard.CardHeader)
class CardTextProps: BaseMUIProps()
object CardText: ReactExternalComponentSpec<CardTextProps>(MaterialUiCard.CardText)
object CardActions: ReactExternalComponentSpec<BaseMUIProps>(MaterialUiCard.CardActions)

class SliderProps(var value: Int = 0,
                  var min: Int = 0,
                  var max: Int = 100,
                  var step: Int = 1,
                  var disabled: Boolean = false,
                  var onChange: (Event, Int) -> Unit): BaseMUIProps()
object Slider: ReactExternalComponentSpec<SliderProps>(MaterialUi.Slider)