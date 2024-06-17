package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.AudioPlayerState

// интерфейс взаимодействия
interface AudioPlayerInteractor {
    fun preparePlayer(url : String?)

    fun startPlayer()

    fun pausePlayer()

    fun setStateListener(listener: OnAudioPlayerStateChangeListener)

    fun getState() : AudioPlayerState

    fun releasePlayer()

    fun getCurrent() : Int
}