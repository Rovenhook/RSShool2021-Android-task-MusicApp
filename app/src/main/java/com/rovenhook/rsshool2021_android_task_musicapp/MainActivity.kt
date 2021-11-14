package com.rovenhook.rsshool2021_android_task_musicapp


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rovenhook.rsshool2021_android_task_musicapp.databinding.ActivityMainBinding
import com.rovenhook.rsshool2021_android_task_musicapp.sevices.MediaService


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
//    private val viewModel: TrackViewModel by viewModels<TrackViewModel>()

    private var mediaServiceBinder: MediaService.MediaServiceBinder? = null
    private var mediaController: MediaControllerCompat? = null
    private var callback: MediaControllerCompat.Callback? = null
    private var serviceConnection: ServiceConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        callback = object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                state?.let {
                    val playing = it.state == PlaybackStateCompat.STATE_PLAYING
                    binding.buttonPlay.isEnabled = !playing
                    binding.buttonPause.isEnabled = playing
                    binding.buttonStop.isEnabled = playing

                    when (it.state) {
                        PlaybackStateCompat.STATE_PLAYING -> callbackPlay()
                        PlaybackStateCompat.STATE_PAUSED -> callbackPause()
                        PlaybackStateCompat.STATE_STOPPED -> callbackStop()
                        PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> callbackNext()
                        PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS -> callbackPrev()
                        else -> callbackUnknown()
                    }
                }
            }
        }

        serviceConnection = object : ServiceConnection {

            override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
                mediaServiceBinder = service as MediaService.MediaServiceBinder
                try {
                    mediaController = MediaControllerCompat(
                        this@MainActivity,
                        mediaServiceBinder?.getMediaSessionToken()!!
                    )
                    mediaController?.registerCallback(callback as MediaControllerCompat.Callback)
                    callback?.onPlaybackStateChanged(mediaController?.playbackState)
                    mediaController?.transportControls?.play()
                } catch (e: RemoteException) {
                    mediaController = null
                }
            }

            override fun onServiceDisconnected(className: ComponentName?) {
                mediaServiceBinder = null
                if (mediaController != null) {
                    mediaController?.unregisterCallback(callback as MediaControllerCompat.Callback)
                    mediaController = null
                }
            }
        }

        bindService(
            Intent(this, MediaService::class.java),
            serviceConnection!!,
            Context.BIND_AUTO_CREATE
        )

        binding.buttonPrev.setOnClickListener { previousTrack() }
        binding.buttonPlay.setOnClickListener { playTrack() }
        binding.buttonStop.setOnClickListener { stopPlaying() }
        binding.buttonPause.setOnClickListener { pausePlaying() }
        binding.buttonNext.setOnClickListener { nextTrack() }
    }

    private fun nextTrack() {
        mediaController?.transportControls?.skipToNext()
    }

    private fun pausePlaying() {
        mediaController?.transportControls?.pause()
    }

    private fun stopPlaying() {
        mediaController?.transportControls?.stop()
    }

    private fun playTrack() {
        mediaController?.transportControls?.play()
    }

    private fun previousTrack() {
        mediaController?.transportControls?.skipToPrevious()
    }

    private fun callbackNext() {
        val description = mediaController?.metadata?.description ?: return
        addToListOfEvents("next track ${description.title} was chosen...\n")
        binding.imageView.setImageBitmap(description.iconBitmap)
        binding.textViewTrackInfo.text = description.title
    }

    private fun callbackPause() {
        val description = mediaController?.metadata?.description ?: return
        addToListOfEvents("track ${description.title} was paused...\n")
    }

    private fun callbackStop() {
        val description = mediaController?.metadata?.description ?: return
        addToListOfEvents("track ${description.title} was stopped...\n")
    }

    private fun callbackPlay() {
        val description = mediaController?.metadata?.description ?: return
        addToListOfEvents("track ${description.title} is playing...\n")
        binding.imageView.setImageBitmap(description.iconBitmap)
        binding.textViewTrackInfo.text = description.title
    }

    private fun callbackPrev() {
        val description = mediaController?.metadata?.description ?: return
        addToListOfEvents("previous track ${description.title} was chosen...\n")
        binding.imageView.setImageBitmap(description.iconBitmap)
        binding.textViewTrackInfo.text = description.title
    }

    private fun callbackUnknown() {
        addToListOfEvents("Unknown playback state change...\n")
    }

    private fun addToListOfEvents(str: String) {
        binding.textViewListOfEVents.append(str)

        binding.scrollViewEvents.post { Runnable {
            binding.scrollViewEvents.fullScroll((View.FOCUS_DOWN))
        }.run() }

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaServiceBinder = null
        if (mediaController != null) {
            mediaController?.unregisterCallback(callback as MediaControllerCompat.Callback)
            mediaController = null
        }
        unbindService(serviceConnection!!)
    }

    companion object {

        private const val BUTTON_PLAY = 1
        private const val BUTTON_STOP = 2
        private const val BUTTON_PAUSE = 3
        private const val BUTTON_NEXT = 4
        private const val BUTTON_PREVIOUS = 5
    }
}
