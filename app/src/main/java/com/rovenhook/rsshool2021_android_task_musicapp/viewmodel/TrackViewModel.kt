package com.rovenhook.rsshool2021_android_task_musicapp.viewmodel

import android.app.Notification
import android.app.PendingIntent
import android.media.MediaPlayer
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rovenhook.rsshool2021_android_task_musicapp.R
import com.rovenhook.rsshool2021_android_task_musicapp.data.Track
import com.rovenhook.rsshool2021_android_task_musicapp.data.TrackRepository
import com.rovenhook.rsshool2021_android_task_musicapp.utils.MyApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackViewModel(
//    private val trackRepository: TrackRepository
) : ViewModel() {
    private var tracks: List<Track> = listOf()
    private val trackRepository: TrackRepository = TrackRepository()
    private val player: MediaPlayer = MediaPlayer()
    private var currentTrackNumber: Int = 0
    private val currentTrack: MutableLiveData<Track> = MutableLiveData()
    private val currentPosition: MutableLiveData<Int> = MutableLiveData(0)
    private val listOfEVents: MutableLiveData<String> = MutableLiveData("")
    private var isStopped: Boolean = true

    fun getCurrentTrack(): LiveData<Track> {
        tracks = trackRepository.getTracksList()
        preparePlayer()
        currentTrack.value = tracks[currentTrackNumber]
        player.setOnCompletionListener {
            if (currentTrackNumber < tracks.size - 1) {
                next()
            }
        }
        return currentTrack
    }

    fun getCurrentPosition(): LiveData<Int> = currentPosition

    fun preparePlayer() {
        player.setDataSource(tracks[currentTrackNumber].trackUri)
        player.prepare()
    }

    fun start() {
        if (!player.isPlaying) {
            if (isStopped) {
                player.reset()
                preparePlayer()
                Log.e("log-tag", "Pressed play after stop")
//                buildNotification()
            }
            player.start()
            isStopped = false
            startPositionCallback()
            listOfEVents.value = String
                .format(
                    "\n%s - %s is playing",
                    tracks[currentTrackNumber].artist,
                    tracks[currentTrackNumber].title
                ) + listOfEVents.value
        }
    }

    fun pause() {
        if (player.isPlaying) {
            listOfEVents.value = String
                .format(
                    "\n%s - %s is paused",
                    tracks[currentTrackNumber].artist,
                    tracks[currentTrackNumber].title
                ) + listOfEVents.value
            player.pause()
        }
    }

    fun stop() {
        if (!isStopped) {
            player.stop()
            isStopped = true
            currentPosition.value = 0
            listOfEVents.value = String
                .format("\n%s", "Player is stopped") + listOfEVents.value
        }
    }

    fun previous() {
        if (currentTrackNumber <= 0) currentTrackNumber = 0 else currentTrackNumber--
        currentTrack.value = tracks[currentTrackNumber]
        player.reset()
        preparePlayer()
        player.start()
        listOfEVents.value = String
            .format(
                "\n%s - %s is playing", tracks[currentTrackNumber].artist,
                tracks[currentTrackNumber].title
            ) + listOfEVents.value
    }

    fun next() {
        if (currentTrackNumber >= tracks.size - 1) {
            currentTrackNumber = tracks.size - 1
        } else {
            currentTrackNumber++
        }
        currentTrack.value = tracks[currentTrackNumber]
        player.reset()
        preparePlayer()
        player.start()
        listOfEVents.value = String
            .format(
                "\n%s - %s is playing",
                tracks[currentTrackNumber].artist,
                tracks[currentTrackNumber].title
            ) + listOfEVents.value
    }

    fun seekTo(timing: Int) {
        Log.e("log-tag", "vm timing = ${timing}")
        player.seekTo(timing * 1000)
        Log.e("log-tag", "vm currentPosition = ${player.currentPosition}")
    }

    fun startPositionCallback() {
        viewModelScope.launch {
            while (player.isPlaying) {
                currentPosition.value = player.currentPosition / 1000
                Log.e("log-tag", "coroutine scope currentPosition: ${currentPosition.value}")
                delay(500)
            }
        }
    }

    fun getListOfPlayerEvents(): LiveData<String> {
        return listOfEVents
    }

//    fun buildNotification() {
//        val builder = NotificationCompat.Builder(MyApplication.getInstance(), MyApplication.CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_launcher_background)
//            .setContentTitle("textTitle")
//            .setContentText("textContent")
//            .setPriority(NotificationCompat.PRIORITY_MAX)
//
//
//        val notification: Notification = builder.build()
//        with(NotificationManagerCompat.from(MyApplication.getInstance())) {
//            notify(1, notification)
//        }
//    }
}
