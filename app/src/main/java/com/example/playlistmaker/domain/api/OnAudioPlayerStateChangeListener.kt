package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.AudioPlayerState

fun interface OnAudioPlayerStateChangeListener{
    fun onChange(state: AudioPlayerState)
}