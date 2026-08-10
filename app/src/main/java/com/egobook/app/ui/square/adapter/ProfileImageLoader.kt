package com.egobook.app.ui.square.adapter

import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.view.doOnLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.egobook.app.ui.square.ProfileImagePlacement
import com.egobook.app.ui.square.calculateProfileImageTransform
import com.egobook.app.ui.square.hasRemoteProfileImage
import com.egobook.app.ui.square.profileTurtleScaleX
import com.egobook.app.R
import java.util.WeakHashMap

private val profileLayoutListeners = WeakHashMap<ImageView, View.OnLayoutChangeListener>()

internal fun ImageView.loadProfileBackground(url: String?, placement: ProfileImagePlacement) {
    if (!hasRemoteProfileImage(url)) {
        Glide.with(this).clear(this)
        stopObservingProfileFrameChanges()
        scaleType = ImageView.ScaleType.CENTER_CROP
        setImageResource(R.drawable.default_background)
        return
    }
    scaleType = ImageView.ScaleType.MATRIX
    observeProfileFrameChanges(placement)
    Glide.with(this)
        .load(url)
        .listener(profilePlacementListener(placement))
        .into(this)
}

internal fun ImageView.loadProfileTurtle(
    url: String?,
    @DrawableRes fallback: Int,
    placement: ProfileImagePlacement,
    mirrorFallback: Boolean = false
) {
    scaleX = profileTurtleScaleX(url, mirrorFallback)
    if (!hasRemoteProfileImage(url)) {
        Glide.with(this).clear(this)
        stopObservingProfileFrameChanges()
        scaleType = ImageView.ScaleType.FIT_CENTER
        setImageResource(fallback)
        return
    }
    scaleType = ImageView.ScaleType.MATRIX
    observeProfileFrameChanges(placement)
    Glide.with(this)
        .load(url)
        .fallback(fallback)
        .error(fallback)
        .listener(
            profilePlacementListener(
                placement,
                turtleFallbackScaleX = profileTurtleScaleX(
                    url,
                    mirrorFallback,
                    isFallbackDisplayed = true
                )
            )
        )
        .into(this)
}

private fun ImageView.profilePlacementListener(
    placement: ProfileImagePlacement,
    turtleFallbackScaleX: Float? = null
) =
    object : RequestListener<Drawable> {
        override fun onLoadFailed(
            error: GlideException?,
            model: Any?,
            target: Target<Drawable>,
            isFirstResource: Boolean
        ): Boolean {
            if (turtleFallbackScaleX != null) {
                scaleX = turtleFallbackScaleX
                stopObservingProfileFrameChanges()
                scaleType = ImageView.ScaleType.FIT_CENTER
            } else {
                post { applyProfilePlacementWhenLaidOut(placement) }
            }
            return false
        }

        override fun onResourceReady(
            resource: Drawable,
            model: Any,
            target: Target<Drawable>?,
            dataSource: DataSource,
            isFirstResource: Boolean
        ): Boolean {
            if (turtleFallbackScaleX != null) {
                scaleX = 1f
                scaleType = ImageView.ScaleType.MATRIX
            }
            post { applyProfilePlacementWhenLaidOut(placement) }
            return false
        }
    }

private fun ImageView.observeProfileFrameChanges(placement: ProfileImagePlacement) {
    stopObservingProfileFrameChanges()
    val listener = View.OnLayoutChangeListener { view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
        if (right - left != oldRight - oldLeft || bottom - top != oldBottom - oldTop) {
            (view as ImageView).applyProfilePlacement(placement)
        }
    }
    profileLayoutListeners[this] = listener
    addOnLayoutChangeListener(listener)
}

private fun ImageView.stopObservingProfileFrameChanges() {
    profileLayoutListeners.remove(this)?.let(::removeOnLayoutChangeListener)
}

private fun ImageView.applyProfilePlacementWhenLaidOut(placement: ProfileImagePlacement) {
    doOnLayout { applyProfilePlacement(placement) }
}

private fun ImageView.applyProfilePlacement(placement: ProfileImagePlacement) {
    val currentDrawable = drawable ?: return
    val drawableWidth = currentDrawable.intrinsicWidth.takeIf { it > 0 } ?: return
    val drawableHeight = currentDrawable.intrinsicHeight.takeIf { it > 0 } ?: return
    val contentWidth = width - paddingLeft - paddingRight
    val contentHeight = height - paddingTop - paddingBottom
    if (contentWidth <= 0 || contentHeight <= 0) return

    val transform = calculateProfileImageTransform(
        frameWidth = contentWidth,
        frameHeight = contentHeight,
        imageWidth = drawableWidth,
        imageHeight = drawableHeight,
        placement = placement
    )

    imageMatrix = Matrix().apply {
        setScale(transform.scale, transform.scale)
        postTranslate(transform.translateX, transform.translateY)
    }
}
