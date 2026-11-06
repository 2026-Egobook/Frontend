package com.egobook.app.data.share

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.egobook.app.R
import com.egobook.app.store.data.model.ItemType
import com.egobook.app.store.data.network.ProfileImageDto
import com.egobook.app.store.ui.CustomItem
import com.egobook.app.store.ui.ItemImage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** All effects are drawn into the same PNG used for preview, gallery and sharing. */
class TurtleShareRenderer @Inject constructor(@ApplicationContext private val context: Context) {
    suspend fun render(profile: ProfileImageDto?, items: List<CustomItem>): Bitmap {
        fun itemUrl(type: ItemType) = (items.find { it.type == type }?.outfitImage as? ItemImage.Url)?.path
        suspend fun load(url: String?, fallback: Int? = null): Drawable? {
            if (url.isNullOrBlank()) return fallback?.let { ContextCompat.getDrawable(context, it) }
            val result = context.imageLoader.execute(ImageRequest.Builder(context).data(url)
                .allowHardware(false).size(WIDTH, HEIGHT).build())
            return (result as? SuccessResult)?.drawable ?: throw java.io.IOException("Could not load share image")
        }
        val background = load(if (profile != null) profile.backgroundImageUrl else itemUrl(ItemType.BACKGROUND), R.drawable.default_background)
        // A confirmed turtle is already a complete composite: never overlay equipment again.
        val layers = if (profile != null) {
            listOfNotNull(load(profile.turtleImageUrl, R.drawable.default_turtle))
        } else {
            // Existing installations may not yet have a cached confirm response. Reuse home layers.
            val urls = listOf(ItemType.BACK, ItemType.SKIN, ItemType.DECO_1, ItemType.DECO_2)
                .mapNotNull { type -> itemUrl(type)?.takeUnless { it.isBlank() || ((type == ItemType.DECO_1 || type == ItemType.DECO_2) && it.contains("Default", true)) } }
            if (urls.isEmpty()) listOfNotNull(load(null, R.drawable.default_turtle))
            else urls.mapNotNull { load(it) }
        }
        return withContext(Dispatchers.Default) {
            val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
            try {
                val canvas = Canvas(bitmap)
                val neutral = ContextCompat.getColor(context, R.color.neutral)
                val paper = ContextCompat.getColor(context, R.color.layer_white)
                // Blur the same background at one-third resolution so its details remain recognizable.
                val blurred = Bitmap.createBitmap(360, 640, Bitmap.Config.ARGB_8888)
                try {
                    val small = Canvas(blurred)
                    small.drawColor(paper)
                    background?.let { drawImage(small, it, RectF(0f, 0f, 360f, 640f), true) }
                    blurOpaque(blurred, radius = 2)
                    small.drawColor(Color.argb(10, 255, 255, 255))
                    canvas.drawBitmap(blurred, null, RectF(0f, 0f, WIDTH.toFloat(), HEIGHT.toFloat()),
                        Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
                } finally { blurred.recycle() }

                val card = RectF(108f, 480f, 972f, 1344f)
                val radius = 44f
                val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = paper
                    setShadowLayer(24f, 0f, 12f, ColorUtils.setAlphaComponent(neutral, 34))
                }
                canvas.drawRoundRect(card, radius, radius, paint)
                paint.clearShadowLayer()
                val save = canvas.save()
                canvas.clipPath(Path().apply { addRoundRect(card, radius, radius, Path.Direction.CW) })
                background?.let { drawImage(canvas, it, card, true) }
                val turtleWidth = card.width() * 0.76f
                val turtleHeight = turtleWidth * 197f / 304f
                val turtle = RectF(card.centerX() - turtleWidth / 2, card.centerY() - turtleHeight / 2,
                    card.centerX() + turtleWidth / 2, card.centerY() + turtleHeight / 2)
                layers.forEach { drawImage(canvas, it, turtle, false) }
                canvas.restoreToCount(save)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 2.5f
                paint.color = ColorUtils.setAlphaComponent(paper, 220)
                canvas.drawRoundRect(card, radius, radius, paint)

                paint.style = Paint.Style.FILL
                paint.color = Color.WHITE
                paint.textSize = 38f
                paint.textAlign = Paint.Align.CENTER
                paint.typeface = ResourcesCompat.getFont(context, R.font.dm_sans_medium)
                paint.setShadowLayer(5f, 0f, 2f, Color.argb(120, 0, 0, 0))
                val account = context.getString(R.string.turtle_share_account)
                val centerY = card.bottom + 64f
                canvas.drawText(account, WIDTH / 2f, centerY - (paint.ascent() + paint.descent()) / 2, paint)
                paint.clearShadowLayer()
                bitmap
            } catch (error: Throwable) {
                bitmap.recycle()
                throw error
            }
        }
    }

    /** Three separable box passes approximate a soft Gaussian blur in bounded memory. */
    private fun blurOpaque(bitmap: Bitmap, radius: Int) {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        val horizontal = IntArray(pixels.size)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        val diameter = radius * 2 + 1
        repeat(3) {
            for (y in 0 until height) {
                var red = 0; var green = 0; var blue = 0
                fun update(x: Int, direction: Int) {
                    val color = pixels[y * width + x.coerceIn(0, width - 1)]
                    red += Color.red(color) * direction
                    green += Color.green(color) * direction
                    blue += Color.blue(color) * direction
                }
                for (x in -radius..radius) update(x, 1)
                for (x in 0 until width) {
                    horizontal[y * width + x] = Color.rgb(red / diameter, green / diameter, blue / diameter)
                    update(x - radius, -1); update(x + radius + 1, 1)
                }
            }
            for (x in 0 until width) {
                var red = 0; var green = 0; var blue = 0
                fun update(y: Int, direction: Int) {
                    val color = horizontal[y.coerceIn(0, height - 1) * width + x]
                    red += Color.red(color) * direction
                    green += Color.green(color) * direction
                    blue += Color.blue(color) * direction
                }
                for (y in -radius..radius) update(y, 1)
                for (y in 0 until height) {
                    pixels[y * width + x] = Color.rgb(red / diameter, green / diameter, blue / diameter)
                    update(y - radius, -1); update(y + radius + 1, 1)
                }
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    }

    private fun drawImage(canvas: Canvas, drawable: Drawable, target: RectF, crop: Boolean) {
        val width = drawable.intrinsicWidth.takeIf { it > 0 }?.toFloat() ?: target.width()
        val height = drawable.intrinsicHeight.takeIf { it > 0 }?.toFloat() ?: target.height()
        val scale = if (crop) maxOf(target.width() / width, target.height() / height) else minOf(target.width() / width, target.height() / height)
        val left = target.centerX() - width * scale / 2f
        val top = target.centerY() - height * scale / 2f
        val save = canvas.save()
        canvas.clipRect(target)
        drawable.setBounds(left.toInt(), top.toInt(), (left + width * scale).toInt(), (top + height * scale).toInt())
        drawable.draw(canvas)
        canvas.restoreToCount(save)
    }

    companion object { const val WIDTH = 1080; const val HEIGHT = 1920 }
}
