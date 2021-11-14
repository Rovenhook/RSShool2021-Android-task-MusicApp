package com.rovenhook.rsshool2021_android_task_musicapp.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JsonTrack(
    val title: String,
    val artist: String,
    val bitmapUri: String,
    val trackUri: String,
    val duration: Long
)
