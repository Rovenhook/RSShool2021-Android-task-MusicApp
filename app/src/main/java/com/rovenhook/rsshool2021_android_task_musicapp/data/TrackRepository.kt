package com.rovenhook.rsshool2021_android_task_musicapp.data

import com.rovenhook.rsshool2021_android_task_musicapp.utils.MyApplication
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class TrackRepository {
    private lateinit var _tracks: List<Track>
    private val tracks: List<Track> get() = requireNotNull(_tracks)
    private val appContext = MyApplication.getInstance()

    fun getTracksList(): List<Track> {
        try {
            val moshi = Moshi.Builder().build()
            val arrayType = Types.newParameterizedType(List::class.java, Track::class.java)
            val adapter: JsonAdapter<List<Track>> = moshi.adapter(arrayType)
            val file = "playlist.json"
            val myJson: String = appContext.assets.open(file).bufferedReader().use { it.readText() }
            _tracks = adapter.fromJson(myJson)!!
        } catch (e: Exception) {
            e.printStackTrace()
            return listOf()
        }
        return tracks
    }
}
