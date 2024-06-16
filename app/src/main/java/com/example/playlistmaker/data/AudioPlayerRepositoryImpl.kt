package com.example.playlistmaker.data

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.AudioPlayerRepository
import com.example.playlistmaker.domain.api.OnAudioPlayerStateChangeListener
import com.example.playlistmaker.domain.models.AudioPlayerState

// конкретная реализация работы репозитория (хранилища самого плеера),
// гарантирующая набор требуемых функций
class AudioPlayerRepositoryImpl() :AudioPlayerRepository {
    private val mediaPlayer = MediaPlayer()
    private var state = AudioPlayerState.DEFAULT
    private lateinit var onStateChangeListener : OnAudioPlayerStateChangeListener

    override fun preparePlayer(url: String?) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            state = AudioPlayerState.PREPARED
            onStateChangeListener.onChange(state)
        }
        mediaPlayer.setOnCompletionListener {
            state = AudioPlayerState.PREPARED
            onStateChangeListener.onChange(state)
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        state = AudioPlayerState.PLAYING
        onStateChangeListener.onChange(state)
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        state = AudioPlayerState.PAUSED
        onStateChangeListener.onChange(state)
    }

    override fun setOnAudioPlayerStateChangeListener(listener: OnAudioPlayerStateChangeListener){
        onStateChangeListener = listener
    }

    override fun getState(): AudioPlayerState {
        return state
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrent(): Int {
        return mediaPlayer.currentPosition
    }

}