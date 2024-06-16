package com.example.playlistmaker.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.AudioPlayerState
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.utils.dpToPx
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private var localPlayerState = AudioPlayerState.DEFAULT
    private var playButton : ImageView? = null
    private var pauseButton : ImageView? = null
    private var backArrow : ImageView? = null
    private var trackProgress : TextView? = null

    private val audioInteractor = Creator.provideAudioInteractor()

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        backArrow = findViewById(R.id.backButton)
        backArrow?.setOnClickListener{
            finish()
        }

        val track : Track? = Creator.provideCurrentTrack(intent)
        bindTrackData(track)

        trackProgress = findViewById(R.id.trackProgress)
        // Аудио плеер ----
        audioInteractor.setStateListener { state ->
            localPlayerState = state
            updatePlayerUI(state)
        }
        audioInteractor.preparePlayer(track?.previewUrl)
        // ----

        playButton = findViewById(R.id.playButton)
        playButton?.setOnClickListener{
            playbackControl()
        }
        pauseButton = findViewById(R.id.pauseButton)
        pauseButton?.setOnClickListener{
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        audioInteractor.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioInteractor.releasePlayer()
    }

    private fun playbackControl() {
        when(localPlayerState) {
            AudioPlayerState.PLAYING -> {
                audioInteractor.pausePlayer()
            }
            AudioPlayerState.PAUSED, AudioPlayerState.PREPARED -> {
                audioInteractor.startPlayer()
            }
            else -> {
                // заглушка
            }
        }
    }

    private fun updatePlayerUI(state: AudioPlayerState){
        when(state) {
            AudioPlayerState.PLAYING -> {
                playButton?.isVisible = false
                pauseButton?.isVisible = true
                handler.post(trackProgressRunnable)
            }
            AudioPlayerState.PAUSED -> {
                playButton?.isVisible = true
                pauseButton?.isVisible = false
                handler.removeCallbacks(trackProgressRunnable)
            }
            AudioPlayerState.PREPARED -> {
                playButton?.isVisible = true
                pauseButton?.isVisible = false
                handler.removeCallbacks(trackProgressRunnable)
                trackProgress?.text = getString(R.string.zero_track_progress)
            }
            AudioPlayerState.DEFAULT -> {
                playButton?.isVisible = true
                pauseButton?.isVisible = false
                trackProgress?.text = getString(R.string.zero_track_progress)
            }
        }
    }


    private val trackProgressRunnable = object : Runnable {
        override fun run() {
            trackProgress?.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(audioInteractor.getCurrent())
            if(localPlayerState == AudioPlayerState.PLAYING)
                handler.postDelayed(this, 300L)
            else if (localPlayerState == AudioPlayerState.PAUSED)
                handler.removeCallbacks(this)
        }
    }

    private fun bindTrackData(track: Track?){
        val trackNameView = findViewById<TextView>(R.id.trackNameView)
        trackNameView.text = track?.trackName

        val artistNameView = findViewById<TextView>(R.id.artistNameView)
        artistNameView.text = track?.artistName

        val artworkView : ImageView = findViewById(R.id.artworkView)
        Glide.with(this)
            .load(track?.getCoverArtwork())
            .fitCenter()
            .transform(RoundedCorners(dpToPx(8.0f, this)))
            .placeholder(R.drawable.artwork_placeholder_big)
            .into(artworkView)

        val trackDurationView = findViewById<TextView>(R.id.trackDuration)
        trackDurationView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track?.trackTimeMillis)

        val trackAlbumView = findViewById<TextView>(R.id.trackAlbum)
        trackAlbumView.text = track?.collectionName

        val trackYearView = findViewById<TextView>(R.id.trackYear)
        trackYearView.text = track?.releaseDate?.subSequence(0,4)

        val trackGenreView = findViewById<TextView>(R.id.trackGenre)
        trackGenreView.text = track?.primaryGenreName

        val trackCountryView = findViewById<TextView>(R.id.trackCountry)
        trackCountryView.text = track?.country
    }
}