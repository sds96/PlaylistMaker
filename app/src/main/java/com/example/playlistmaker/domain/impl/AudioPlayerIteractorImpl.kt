package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.AudioPlayerInteractor
import com.example.playlistmaker.domain.api.AudioPlayerRepository
import com.example.playlistmaker.domain.api.OnAudioPlayerStateChangeListener
import com.example.playlistmaker.domain.models.AudioPlayerState

// реализация интерфейса взаимодействия
class AudioPlayerIteractorImpl(private val repository : AudioPlayerRepository) : AudioPlayerInteractor {
    override fun preparePlayer(url: String?) {
        repository.preparePlayer(url)
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun setStateListener(listener: OnAudioPlayerStateChangeListener) {
        repository.setOnAudioPlayerStateChangeListener(listener)
    }

    override fun getState(): AudioPlayerState {
        return repository.getState()
    }

    override fun releasePlayer() {
        repository.releasePlayer()
    }

    override fun getCurrent() : Int {
        return repository.getCurrent()
    }

}