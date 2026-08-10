package com.egobook.app.ui.square

fun hasRemoteProfileImage(url: String?): Boolean = !url.isNullOrBlank()

fun profileTurtleScaleX(url: String?, isFallbackDisplayed: Boolean = false): Float =
    if (hasRemoteProfileImage(url) && !isFallbackDisplayed) 1f else -1f
