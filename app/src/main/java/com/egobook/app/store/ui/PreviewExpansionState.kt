package com.egobook.app.store.ui

enum class PreviewExpansionState {
    COLLAPSED, EXPANDED;

    fun toggle(): PreviewExpansionState = when (this) {
        COLLAPSED -> EXPANDED
        EXPANDED -> COLLAPSED
    }

    val isExpanded: Boolean get() = this == EXPANDED
}
