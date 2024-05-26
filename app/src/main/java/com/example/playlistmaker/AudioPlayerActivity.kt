package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

const val PARCEL_KEY="track"

class AudioPlayerActivity : AppCompatActivity() {
    enum class State {
        DEFAULT,
        PREPARED,
        PLAYING,
        PAUSED
    }

    private var playerState = State.DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var playButton : ImageView? = null
    private var pauseButton : ImageView? = null
    private var backArrow : ImageView? = null
    private var trackProgress : TextView? = null

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        backArrow = findViewById(R.id.backButton)

        backArrow?.setOnClickListener{
            finish()
        }


        val track : Track? = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(PARCEL_KEY, Track::class.java)
        else
            intent.getParcelableExtra(PARCEL_KEY) as Track?

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

        trackProgress = findViewById(R.id.trackProgress)

        preparePlayer(track?.previewUrl)
        playButton?.setOnClickListener{
            playbackControl()
        }
        pauseButton?.setOnClickListener{
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun preparePlayer(url : String?) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton?.isVisible = true
            pauseButton?.isVisible = false
            playerState = State.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton?.isVisible = true
            pauseButton?.isVisible = false
            playerState = State.PREPARED
            handler.removeCallbacks(trackProgressRunnable)
            trackProgress?.text = R.string.zero_track_progress.toString()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton?.isVisible = false
        pauseButton?.isVisible = true
        playerState = State.PLAYING
        handler.post(trackProgressRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton?.isVisible = true
        pauseButton?.isVisible = false
        playerState = State.PAUSED
    }

    private fun playbackControl() {
        when(playerState) {
            State.PLAYING -> {
                pausePlayer()
            }
            State.PREPARED, State.PAUSED -> {
                startPlayer()
            }
            else -> {
                // заглушка
            }
        }
    }

    private val trackProgressRunnable = object : Runnable {
        override fun run() {
            trackProgress?.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            if(playerState == State.PLAYING)
                handler.postDelayed(this, 300L)
            else if (playerState == State.PAUSED)
                handler.removeCallbacks(this)
        }
    }
}