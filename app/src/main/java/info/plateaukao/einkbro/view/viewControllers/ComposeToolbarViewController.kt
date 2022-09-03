package info.plateaukao.einkbro.view.viewControllers

import android.content.Context
import android.util.AttributeSet
import android.view.View.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.AbstractComposeView
import info.plateaukao.einkbro.preference.ConfigManager
import info.plateaukao.einkbro.view.Album
import info.plateaukao.einkbro.view.compose.ComposedToolbar
import info.plateaukao.einkbro.view.compose.MyTheme
import info.plateaukao.einkbro.view.toolbaricons.ToolbarAction
import info.plateaukao.einkbro.view.toolbaricons.ToolbarAction.*
import info.plateaukao.einkbro.view.toolbaricons.ToolbarActionInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ComposeToolbarViewController(
    private val toolbarComposeView: ToolbarComposeView,
    private val onIconClick: (ToolbarAction) -> Unit,
    private val onIconLongClick: (ToolbarAction) -> Unit,
    private val onTabClick: (Album) -> Unit,
    private val onTabLongClick: (Album) -> Unit,
) : KoinComponent {
    private val config: ConfigManager by inject()

    private val readerToolbarActions: List<ToolbarAction> = listOf(
        RotateScreen,
        FullScreen,
        BoldFont,
        Font,
        Touch,
        TOC,
        Settings,
        CloseTab,
    )

    private var isLoading: Boolean = false

    private var isReader: Boolean = false

    private val currentAlbumList = mutableListOf<Album>()

    private val albumsState = mutableStateOf(currentAlbumList.toList())

    fun showTabbar(shouldShow: Boolean) {
        toolbarComposeView.shouldShowTabs = shouldShow
    }

    fun updateTabView(albumList: List<Album>) {
        albumsState.value = albumList.toList()
        toolbarComposeView.focusedAlbumIndex.value =
            albumsState.value.indexOfFirst { it.isActivated }
    }

    init {
        toolbarComposeView.apply {
            val iconEnums = if (isReader) readerToolbarActions else config.toolbarActions
            toolbarActionInfoList = iconEnums.toToolbarActionInfoList()

            shouldShowTabs = config.shouldShowTabBar
            onItemClick = onIconClick
            onItemLongClick = onIconLongClick
            albumList = albumsState
            focusedAlbumIndex.value = albumsState.value.indexOfFirst { it.isActivated }
        }

        toolbarComposeView.onTabClick = onTabClick
        toolbarComposeView.onTabLongClick = onTabLongClick
    }

    fun isDisplayed(): Boolean = toolbarComposeView.visibility == VISIBLE

    fun show() = toggleIconsOnOmnibox(true)

    fun hide() = toggleIconsOnOmnibox(false)

    fun updateTabCount(text: String) {
        toolbarComposeView.tabCount = text
    }

    fun updateRefresh(isLoadingWeb: Boolean) {
        if (isLoadingWeb == isLoading) return

        isLoading = isLoadingWeb
        updateIcons()
    }

    fun setEpubReaderMode() {
        isReader = true
        updateIcons()
    }

    fun updateIcons() {
        val iconEnums = if (isReader) readerToolbarActions else config.toolbarActions
        toolbarComposeView.toolbarActionInfoList = iconEnums.toToolbarActionInfoList()
        toolbarComposeView.isIncognito = config.isIncognitoMode
    }

    private fun List<ToolbarAction>.toToolbarActionInfoList(): List<ToolbarActionInfo> {
        return this.map { toolbarAction ->
            when (toolbarAction) {
                BoldFont -> ToolbarActionInfo(toolbarAction, config.boldFontStyle)
                Refresh -> ToolbarActionInfo(toolbarAction, isLoading)
                Desktop -> ToolbarActionInfo(toolbarAction, config.desktop)
                Touch -> ToolbarActionInfo(toolbarAction, config.enableTouchTurn)
                else -> ToolbarActionInfo(toolbarAction, false)
            }
        }
    }

    private fun toggleIconsOnOmnibox(shouldShow: Boolean) {
        toolbarComposeView.visibility = if (shouldShow) VISIBLE else INVISIBLE
    }

    fun updateTitle(title: String) {
        toolbarComposeView.title = title
    }
}

class ToolbarComposeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : AbstractComposeView(context, attrs, defStyle) {

    var toolbarActionInfoList: List<ToolbarActionInfo> by mutableStateOf(emptyList())
    var shouldShowTabs by mutableStateOf(false)
    var title by mutableStateOf("")
    var tabCount by mutableStateOf("")
    var isIncognito by mutableStateOf(false)
    var onItemClick: (ToolbarAction) -> Unit = {}
    var onItemLongClick: (ToolbarAction) -> Unit = {}
    var onTabClick: (Album) -> Unit = {}
    var onTabLongClick: (Album) -> Unit = {}

    var albumList = mutableStateOf(listOf<Album>())
    var focusedAlbumIndex = mutableStateOf(0)

    @Composable
    override fun Content() {
        MyTheme {
            ComposedToolbar(
                showTabs = shouldShowTabs,
                toolbarActionInfoList,
                title = title,
                tabCount = tabCount,
                isIncognito = isIncognito,
                onIconClick = onItemClick,
                onIconLongClick = onItemLongClick,
                albumList = albumList,
                focusedAlbumIndex = focusedAlbumIndex,
                onAlbumClick = onTabClick,
                onAlbumLongClick = onTabLongClick,
            )
        }
    }
}