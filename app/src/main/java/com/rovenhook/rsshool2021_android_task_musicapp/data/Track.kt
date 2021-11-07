package com.rovenhook.rsshool2021_android_task_musicapp.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Track(
    var title: String,
    var artist: String,
    var bitmapUri: String,
    var trackUri: String,
    var duration: Long
)
