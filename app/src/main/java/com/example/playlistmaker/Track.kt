package com.example.playlistmaker

import java.io.Serializable

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,    // использую только год, это первые 4 символа
    val primaryGenreName: String,
    val country: String,
    val trackTimeMillis: Int = 0
) : Serializable
{
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}