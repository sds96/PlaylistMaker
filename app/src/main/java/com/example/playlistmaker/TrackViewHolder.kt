package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.utils.dpToPx
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
    private val trackName : TextView = itemView.findViewById(R.id.trackName)
    private val artistName : TextView = itemView.findViewById(R.id.artistName)
    private val trackTime : TextView = itemView.findViewById(R.id.trackTime)
    private val artwork : ImageView = itemView.findViewById(R.id.artwork)

    fun bind(track: Track){
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(2.0f, itemView.context)))
            .placeholder(R.drawable.artwork_placeholder)
            .into(artwork)
    }
}