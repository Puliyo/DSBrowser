package de.baumann.browser.view.toolbaricons

enum class ToolbarAction {
    Title, Back, Refresh, Touch, PageUp, PageDown, TabCount, Font, Settings, Bookmark;

    companion object {
        fun fromOrdinal(value: Int) = values().first { it.ordinal == value }
    }
}