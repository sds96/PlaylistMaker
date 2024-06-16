package com.example.playlistmaker

import android.content.Intent
import android.os.Build
import com.example.playlistmaker.data.AudioPlayerRepositoryImpl
import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.api.AudioPlayerRepository
import com.example.playlistmaker.domain.impl.AudioPlayerIteractorImpl
import com.example.playlistmaker.domain.models.Track

object Creator {
    const val PARCEL_KEY="track"
    fun provideCurrentTrack(intent: Intent): Track? {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getParcelableExtra(PARCEL_KEY, Track::class.java)
        else
            intent.getParcelableExtra(PARCEL_KEY) as Track?
    }

    fun provideAudioInteractor() : AudioPlayerInteractor {
        return AudioPlayerIteractorImpl(provideAudioPlayerRepository())
    }

    private fun provideAudioPlayerRepository() : AudioPlayerRepository{
        return AudioPlayerRepositoryImpl()
    }
}