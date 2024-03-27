package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory (private val sharedPreferences: SharedPreferences) {
    // история поиска [new -> old]
    private var trackHistory = read()
    private val MAX_SIZE = 10
    private val SEARCH_HISTORY_KEY = "SHK"

    fun addTrack(track: Track){
        // если трек есть - то удаляется и перемещается
        // если трека нет - remove ничего не делает
        trackHistory.remove(track)
        trackHistory.add(0, track)
        if(trackHistory.size > MAX_SIZE)
            trackHistory.removeLast()

        // обновляем запись в shared_prefs
        write(trackHistory)
    }

    fun getHistory() : List<Track>{
        trackHistory = read()
        return trackHistory
    }
    fun clearHistory(){
        trackHistory.clear()
        write(trackHistory)
    }

    fun isNotEmpty() :Boolean {
        return trackHistory.isNotEmpty()
    }

    private fun read(): ArrayList<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?: return ArrayList()
        return Gson().fromJson(json, object: TypeToken<ArrayList<Track>>(){}.type)
    }

    private fun write(tracks : ArrayList<Track>) {
        val json = Gson().toJson(tracks)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }
}