package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
    private val trackName : TextView = itemView.findViewById(R.id.trackName)
    private val artistName : TextView = itemView.findViewById(R.id.artistName)
    private val trackTime : TextView = itemView.findViewById(R.id.trackTime)
    private val artwork : ImageView = itemView.findViewById(R.id.artwork)

    fun bind(track: Track){
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(2.0f, itemView.context)))
            .placeholder(R.drawable.media_icon)
            .into(artwork)
    }
}

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics).toInt()
}