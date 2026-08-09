package com.egobook.app.ui.square.adapter

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide

internal fun ImageView.loadProfileBackground(url: String?) {
    Glide.with(this)
        .load(url)
        .into(this)
}

internal fun ImageView.loadProfileTurtle(url: String?, @DrawableRes fallback: Int) {
    Glide.with(this)
        .load(url)
        .fallback(fallback)
        .error(fallback)
        .into(this)
}
