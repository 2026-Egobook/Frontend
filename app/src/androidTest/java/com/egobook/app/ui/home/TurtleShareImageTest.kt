package com.egobook.app.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Build
import android.provider.MediaStore
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.egobook.app.R
import com.egobook.app.data.share.TurtleShareRenderer
import com.egobook.app.data.share.TurtleShareStorage
import com.egobook.app.store.data.network.ProfileImageDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class TurtleShareImageTest {
    @Test
    fun defaultImageCanBePreviewedSharedAndSaved() = runBlocking {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val storage = TurtleShareStorage(context)
        val image = TurtleShareRenderer(context).render(ProfileImageDto(null, null), emptyList())
        assertEquals(1080, image.width)
        assertEquals(1920, image.height)
        val background = BitmapFactory.decodeResource(context.resources, R.drawable.default_background)
        val expected = Bitmap.createScaledBitmap(background, 1080, 1920, true)
        // Outer scenery is full bleed and unblurred, including where the old card/account lived.
        for ((x, y) in listOf(80 to 80, 80 to 1500, 500 to 1500, 1000 to 1800)) {
            val wanted = expected.getPixel(x, y)
            val actual = image.getPixel(x, y)
            for (shift in listOf(0, 8, 16)) {
                assertTrue(kotlin.math.abs(((wanted shr shift) and 255) - ((actual shr shift) and 255)) <= 3)
            }
        }
        background.recycle(); expected.recycle()
        val name = storage.writePreview(image)
        val uri = storage.shareUri(name)
        assertEquals("content", uri.scheme)
        val decoded = context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it) }
        assertEquals(1080, decoded.width)
        assertEquals(1920, decoded.height)
        val originalPng = storage.previewFile(name).readBytes()
        context.contentResolver.openInputStream(uri)!!.use { assertArrayEquals(originalPng, it.readBytes()) }
        val output = File(context.getExternalFilesDir(null), "qa-share").apply { mkdirs() }
        storage.previewFile(name).copyTo(File(output, "turtle-image.png"), true)
        if (Build.VERSION.SDK_INT >= 24) {
            val gallery = storage.saveToGallery(name)
            try {
                assertEquals("image/png", context.contentResolver.getType(gallery))
                context.contentResolver.openInputStream(gallery)!!.use { assertArrayEquals(originalPng, it.readBytes()) }
                if (Build.VERSION.SDK_INT >= 29) context.contentResolver.query(gallery, arrayOf(MediaStore.Images.Media.IS_PENDING), null, null, null)!!.use {
                    assertTrue(it.moveToFirst()); assertEquals(0, it.getInt(0))
                }
            } finally { context.contentResolver.delete(gallery, null, null) }
        }
        instrumentation.runOnMainSync {
            val themed = ContextThemeWrapper(context, R.style.Theme_Egobook)
            val root = LayoutInflater.from(themed).inflate(R.layout.dialog_turtle_share_fullscreen, null)
            root.findViewById<ImageView>(R.id.iv_turtle_share_preview).apply {
                setImageBitmap(decoded)
            }
            val width = context.resources.displayMetrics.widthPixels
            val height = context.resources.displayMetrics.heightPixels
            root.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
            root.layout(0, 0, root.measuredWidth, root.measuredHeight)
            assertEquals("저장하기", root.findViewById<TextView>(R.id.btn_turtle_save).text.toString())
            assertEquals("공유하기", root.findViewById<TextView>(R.id.btn_turtle_share).text.toString())
            val preview = Bitmap.createBitmap(root.width, root.height, Bitmap.Config.ARGB_8888)
            root.draw(Canvas(preview))
            val previewView = root.findViewById<ImageView>(R.id.iv_turtle_share_preview)
            assertTrue(previewView.height > height * 0.6f)
            assertTrue(kotlin.math.abs(previewView.width.toFloat() / previewView.height - 9f / 16f) < 0.01f)
            val home = LayoutInflater.from(themed).inflate(R.layout.fragment_home, null)
            home.findViewById<View>(R.id.btn_turtle_share).visibility = View.GONE
            home.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
            home.layout(0, 0, width, height)
            com.egobook.app.databinding.DialogTurtleShareBinding.bind(root)
            val camera = home.findViewById<View>(R.id.btn_turtle_camera)
            val ad = home.findViewById<View>(R.id.iv_ad)
            assertTrue(camera.top >= ad.bottom)
            assertEquals(ad.left, camera.left)
            assertEquals(48, (camera.layoutParams.height / context.resources.displayMetrics.density).toInt())
            File(output, "turtle-share-dialog.png").outputStream().use { preview.compress(Bitmap.CompressFormat.PNG, 100, it) }
            preview.recycle()
        }
        decoded.recycle()
    }
    @Test
    fun confirmedCompositeAndIndependentNullsIgnoreEquippedLayers() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val turtle = File(context.cacheDir, "qa-source-turtle.png")
        val background = File(context.cacheDir, "qa-source-background.png")
        try {
            context.resources.openRawResource(R.drawable.default_turtle).use { source ->
                turtle.outputStream().use { source.copyTo(it) }
            }
            context.resources.openRawResource(R.drawable.default_background).use { source ->
                background.outputStream().use { source.copyTo(it) }
            }
            val invalidLayer = com.egobook.app.store.ui.CustomItem(
                "test", com.egobook.app.store.data.model.ItemType.SKIN,
                com.egobook.app.store.data.model.Price(0), com.egobook.app.store.data.model.ItemStatus.PURCHASED,
                outfitImage = com.egobook.app.store.ui.ItemImage.Url("file:///missing-test-layer.png")
            )
            val profiles = listOf(
                ProfileImageDto(turtle.toURI().toString(), background.toURI().toString()),
                ProfileImageDto(null, background.toURI().toString()),
                ProfileImageDto(turtle.toURI().toString(), null)
            )
            for (profile in profiles) {
                // A cached composite must never attempt to load this invalid equipment layer.
                val image = TurtleShareRenderer(context).render(profile, listOf(invalidLayer))
                try { assertEquals(1080, image.width); assertEquals(1920, image.height) }
                finally { image.recycle() }
            }
        } finally { turtle.delete(); background.delete() }
    }

}
