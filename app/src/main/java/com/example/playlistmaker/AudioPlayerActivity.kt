package com.example.playlistmaker

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

const val PARCEL_KEY="track"

class AudioPlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val backArrow = findViewById<ImageView>(R.id.backButton)
        backArrow.setOnClickListener{
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
    }
}