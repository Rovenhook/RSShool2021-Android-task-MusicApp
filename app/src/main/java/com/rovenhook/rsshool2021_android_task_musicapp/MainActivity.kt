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
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.rovenhook.rsshool2021_android_task_musicapp.databinding.ActivityMainBinding
import com.rovenhook.rsshool2021_android_task_musicapp.viewmodel.TrackViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: TrackViewModel by viewModels<TrackViewModel>()

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





































//        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.translate_animation)
//        binding.textViewTrackInfo.animation = animation
//
//        viewModel.getCurrentTrack().observe(this, Observer { track ->
//            binding.imageView.load(track.bitmapUri)
//            binding.textViewTrackInfo.text = String.format("%s - %s", track.artist, track.title)
//            binding.textViewTiming.text =
//                String.format(
//                    "%d:%02d",
//                    (track.duration / 1000 / 60),
//                    ((track.duration / 1000) % 60)
//                )
//            binding.seekBar.setProgress(0)
//            binding.seekBar.max = (track.duration / 1000).toInt()
//            Log.e("log-tag", "seekbar max = ${binding.seekBar.max}")
//        })
//
//        viewModel.getCurrentPosition().observe(this, Observer { progress ->
//            binding.seekBar.setProgress(progress)
//            binding.textViewTimingCurrent.text =
//                String.format("%d:%02d /", (progress / 60), ((progress) % 60))
//        })
//
//        viewModel.getListOfPlayerEvents().observe(this, Observer { text ->
//            binding.textViewListOfEVents.text = text
//        })
//
//        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                binding.seekBar.setProgress(progress)
//                if (fromUser) {
//                    binding.textViewTimingCurrent.text =
//                        String.format("%d:%02d /", (progress / 60), ((progress) % 60))
//                    viewModel.seekTo(progress)
//                    Log.e("log-tag", "seekbar max = ${binding.seekBar.max}")
//                    Log.e("log-tag", "seekbar progress = ${binding.seekBar.progress}")
//                }
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
//        })
//
//        binding.buttonPlay.setOnClickListener {
//            viewModel.start()
//        }
//        binding.buttonPause.setOnClickListener {
//            viewModel.pause()
//        }
//        binding.buttonStop.setOnClickListener {
//            viewModel.stop()
//        }
//        binding.buttonPrev.setOnClickListener {
//            viewModel.previous()
//        }
//        binding.buttonNext.setOnClickListener {
//            viewModel.next()
//        }
//    }
//}
