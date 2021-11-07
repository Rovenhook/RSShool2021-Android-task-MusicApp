package com.rovenhook.rsshool2021_android_task_musicapp


import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.rovenhook.rsshool2021_android_task_musicapp.data.Track
import com.rovenhook.rsshool2021_android_task_musicapp.databinding.ActivityMainBinding
import com.rovenhook.rsshool2021_android_task_musicapp.viewmodel.TrackViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TrackViewModel by viewModels<TrackViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.translate_animation)
        binding.textViewTrackInfo.animation = animation

        viewModel.getCurrentTrack().observe(this, Observer { track ->
            binding.imageView.load(track.bitmapUri)
            binding.textViewTrackInfo.text = String.format("%s - %s",track.artist, track.title)
            binding.textViewTiming.text =
                String.format("%d:%02d", (track.duration / 1000 / 60), ((track.duration / 1000) % 60))
            binding.seekBar.setProgress(0)
            binding.seekBar.max = (track.duration / 1000).toInt()
            Log.e("log-tag", "seekbar max = ${binding.seekBar.max}")
        })

        viewModel.getCurrentPosition().observe(this, Observer { progress ->
            binding.seekBar.setProgress(progress)
            binding.textViewTimingCurrent.text =
                String.format("%d:%02d /", (progress / 60), ((progress) % 60))
        })

        viewModel.getListOfPlayerEvents().observe(this, Observer { text ->
            binding.textViewListOfEVents.text = text
        })

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.seekBar.setProgress(progress)
                if (fromUser) {
                    binding.textViewTimingCurrent.text =
                        String.format("%d:%02d /", (progress / 60), ((progress) % 60))
                    viewModel.seekTo(progress)
                    Log.e("log-tag", "seekbar max = ${binding.seekBar.max}")
                    Log.e("log-tag", "seekbar progress = ${binding.seekBar.progress}")
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.buttonPlay.setOnClickListener {
            viewModel.start()
        }
        binding.buttonPause.setOnClickListener {
            viewModel.pause()
        }
        binding.buttonStop.setOnClickListener {
            viewModel.stop()
        }
        binding.buttonPrev.setOnClickListener {
            viewModel.previous()
        }
        binding.buttonNext.setOnClickListener {
            viewModel.next()
        }
    }


}
