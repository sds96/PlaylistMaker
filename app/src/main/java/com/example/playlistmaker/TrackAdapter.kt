package com.example.playlistmaker

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.AudioPlayerActivity

class TrackAdapter(
    private val tracks: List<Track>,
    private val clickListener: TrackClickListener
) : RecyclerView.Adapter<TrackViewHolder> ()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener{
            if(clickDebounce()){
                val intent = Intent(holder.itemView.context, AudioPlayerActivity::class.java)
                intent.putExtra(Creator.PARCEL_KEY, tracks[position])
                holder.itemView.context.startActivity(intent)
                clickListener.onTrackClick(tracks[position], position)
            }
        }
    }

    // debounce на элементе поиска
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private fun clickDebounce() : Boolean {
        val currentState = isClickAllowed
        if(isClickAllowed){
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true}, 1000L)
        }
        return currentState
    }

    fun interface TrackClickListener {
        fun onTrackClick(track : Track, position: Int)
    }
}