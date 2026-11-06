package com.egobook.app.data.share

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

class TurtleShareStorage @Inject constructor(@ApplicationContext private val context: Context) {
    private val directory get() = File(context.cacheDir, "turtle-shares")

    suspend fun writePreview(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
        val file = File(directory, "turtle-${UUID.randomUUID()}.png")
        try {
            check(directory.isDirectory || directory.mkdirs()) { "이미지 저장 공간을 만들지 못했습니다." }
            file.outputStream().use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
            file.name
        } catch (error: Exception) {
            file.delete()
            throw error
        } finally { bitmap.recycle() }
    }

    fun previewFile(name: String): File {
        require(File(name).name == name) { "잘못된 이미지 경로입니다." }
        return File(directory, name).also { check(it.isFile) { "이미지가 만료되었습니다. 다시 만들어 주세요." } }
    }

    fun shareUri(name: String): Uri = FileProvider.getUriForFile(context, "${context.packageName}.turtle-share", previewFile(name))

    suspend fun saveToGallery(name: String): Uri = withContext(Dispatchers.IO) {
        val source = previewFile(name)
        val displayName = "EGOBOOK_${System.currentTimeMillis()}.png"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= 29) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/EGOBOOK")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            } else {
                @Suppress("DEPRECATION")
                val pictures = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "EGOBOOK")
                check(pictures.isDirectory || pictures.mkdirs())
                @Suppress("DEPRECATION")
                put(MediaStore.Images.Media.DATA, File(pictures, displayName).absolutePath)
            }
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: throw IOException("갤러리에 저장하지 못했습니다.")
        try {
            resolver.openOutputStream(uri)?.use { output -> source.inputStream().use { it.copyTo(output) } }
                ?: throw IOException("이미지를 기록하지 못했습니다.")
            if (Build.VERSION.SDK_INT >= 29) {
                check(resolver.update(uri, ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }, null, null) == 1) {
                    "Could not publish gallery image"
                }
            }
            uri
        } catch (error: Exception) {
            resolver.delete(uri, null, null) // Roll back only the new image created by this operation.
            throw error
        }
    }
}
