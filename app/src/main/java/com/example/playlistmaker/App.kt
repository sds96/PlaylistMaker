package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val PLAYLISTMAKER_SHARED_PREFERENCES = "playlistmaker_shared_preferences"
class App : Application() {
    private lateinit var sharedPrefs : SharedPreferences

    var darkTheme : Boolean = false
    private val darkThemeKey = "dark_theme_is_on"
    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(PLAYLISTMAKER_SHARED_PREFERENCES, MODE_PRIVATE)

        // первый запуск тема всегда светлая
        darkTheme = sharedPrefs.getBoolean(darkThemeKey, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnable: Boolean){
        darkTheme = darkThemeEnable
        sharedPrefs.edit()
            .putBoolean(darkThemeKey, darkThemeEnable)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if(darkThemeEnable){
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}