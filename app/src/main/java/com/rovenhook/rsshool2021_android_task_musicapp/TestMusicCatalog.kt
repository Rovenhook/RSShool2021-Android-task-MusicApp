package com.rovenhook.rsshool2021_android_task_musicapp

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.bumptech.glide.Glide
import com.rovenhook.rsshool2021_android_task_musicapp.utils.MyApplication
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.*

/**
 * Mock music catalog
 */
class TestMusicCatalog(context: Context) {

    init {
        setCatalogFromJson(context)
    }

    @JsonClass(generateAdapter = true)
    data class JsonTrack(
        val title: String,
        val artist: String,
        val bitmapUri: String,
        val trackUri: String,
        val duration: Long
    )

    val bitmaps = HashMap<String, Bitmap>(5)

    private var _catalog: List<JsonTrack>? = null
    private val catalog: List<JsonTrack> get() = requireNotNull(_catalog)

    @DelicateCoroutinesApi
    private fun setCatalogFromJson(context: Context) {

        Log.e("log-tag", "URL:    ")
        val moshi = Moshi.Builder().build()

        val arrayType = Types.newParameterizedType(List::class.java, JsonTrack::class.java)
        val adapter: JsonAdapter<List<JsonTrack>> = moshi.adapter(arrayType)

        val file = "playlist.json"

        val myJson = context.assets.open(file).bufferedReader().use { it.readText() }
        Log.e("log-tag", "URL:    ")
        _catalog = adapter.fromJson(myJson)

//        Log.e("log-tag", "catalog:    ")
//        _catalog?.forEach {
//            Log.e("log-tag", "URL:   ${it}")
//        }

        GlobalScope.launch(Dispatchers.Default) {
            Log.e("log-tag", "URL:    ")
            try {
                Log.e("log-tag", "URL:   1 ")
                _catalog?.forEach { track ->
                    Log.e("log-tag", "URL:   ${track}")

                    val bitmap =
                        Glide.with(context).asBitmap().load(track.bitmapUri).into(200, 200).get()
                }
            } catch (e: Exception) {
            }
        }
    }

    val maxTrackIndex = catalog.size - 1
    var currentTrackIndex = 0
    val countTracks = catalog.size

    var currentTrack = catalog[0]
        get() = catalog[currentTrackIndex]
        private set

    fun next(): JsonTrack {
        if (currentTrackIndex == maxTrackIndex) {
            currentTrackIndex = 0
        } else {
            currentTrackIndex++
        }
        return currentTrack
    }

    fun previous(): JsonTrack {
        if (currentTrackIndex == 0) {
            currentTrackIndex = maxTrackIndex
        } else {
            currentTrackIndex--
        }
        return currentTrack
    }

    fun getTrackByIndex(index: Int) = catalog[index]
    fun getTrackCatalog() = catalog


    fun getBitmap(jsonTrack: JsonTrack): Bitmap? {
        var bitmap: Bitmap? = null
        CoroutineScope(Dispatchers.IO).launch {
            val loader = ImageLoader(MyApplication.getInstance())
            val request = ImageRequest.Builder(MyApplication.getInstance())
                .data(jsonTrack.bitmapUri)
                .allowHardware(false) // Disable hardware bitmaps.
                .build()

            val result = (loader.execute(request) as SuccessResult).drawable
            bitmap = (result as BitmapDrawable).bitmap
        }
        return bitmap
    }
}
