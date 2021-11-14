package com.rovenhook.rsshool2021_android_task_musicapp.data

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.rovenhook.rsshool2021_android_task_musicapp.utils.MyApplication
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@JsonClass(generateAdapter = true)
data class Track(
    var title: String,
    var artist: String,
    var bitmapUri: String,
    var trackUri: String,
    var duration: Long
) {
    fun getBitmap(): Bitmap? {
        var bitmap: Bitmap? = null
        CoroutineScope(Dispatchers.IO).launch{
            val loader = ImageLoader(MyApplication.getInstance())
            val request = ImageRequest.Builder(MyApplication.getInstance())
                .data(bitmapUri)
                .allowHardware(false) // Disable hardware bitmaps.
                .build()

            val result = (loader.execute(request) as SuccessResult).drawable
            bitmap = (result as BitmapDrawable).bitmap
        }
        return bitmap
    }
}
