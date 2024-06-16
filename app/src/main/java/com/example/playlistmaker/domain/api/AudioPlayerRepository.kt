package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.AudioPlayerState

// репозиторий плеера должен предоставлять набор функций для взаимодействия с его телом
interface AudioPlayerRepository {
    fun preparePlayer(url : String?)

    fun startPlayer()

    fun pausePlayer()

    fun setOnAudioPlayerStateChangeListener(listener: OnAudioPlayerStateChangeListener)

    fun getState() : AudioPlayerState

    fun releasePlayer()

    fun getCurrent() : Int
}