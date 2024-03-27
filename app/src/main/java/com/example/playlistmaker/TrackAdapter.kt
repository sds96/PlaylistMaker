package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter (
    private val tracks: List<Track>,
    private val searchHistory : SearchHistory
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
            searchHistory.addTrack(tracks[position])
            // изменился порядок от начала и до нажатого
            notifyItemRangeChanged(0, position+1)
        }
    }
}