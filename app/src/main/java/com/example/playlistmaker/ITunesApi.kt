package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

class TracksResponse(val results: ArrayList<Track>)
interface ITunesApi {
    @GET("/search")
    fun search(@Query("term") text : String, @Query("entity") entity : String = "song") : Call<TracksResponse>
}