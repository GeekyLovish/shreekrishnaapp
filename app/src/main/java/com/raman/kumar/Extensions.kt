package com.raman.kumar.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object Extensions {

    fun Context.shareImageFromUrl(imageUrl: String) {
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    try {
                        val cachePath = File(cacheDir, "images")
                        cachePath.mkdirs()
                        val imageFile = File(cachePath, "shared_image.jpg")
                        FileOutputStream(imageFile).use { out ->
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, out)
                        }

                        val contentUri: Uri = FileProvider.getUriForFile(
                            this@shareImageFromUrl,
                            "$packageName.fileprovider",
                            imageFile
                        )

                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/*"
                            putExtra(Intent.EXTRA_STREAM, contentUri)
                            putExtra(
                                Intent.EXTRA_TEXT, """
🌿🌿श्री कृष्णा🌿🌿
श्री कृष्ण भगवान की सुन्दर तस्वीरें देखने Download करने व Wallpaper , Video , Ringtone , Bhajan , Use करने के लिए श्री कृष्णा App को Download करें

App Link: https://play.google.com/store/apps/details?id=com.raman.kumar.shrikrishan
                                """.trimIndent()
                            )
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        startActivity(Intent.createChooser(shareIntent, "Share Image"))

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@shareImageFromUrl, "Image sharing failed!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    // Aise aur bhi bana sakte ho:
    fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // aur bhi extensions yahin pe add karo...
}
