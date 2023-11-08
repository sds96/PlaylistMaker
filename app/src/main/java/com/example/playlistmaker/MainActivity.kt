package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.buttonSearch)

        val buttonClickListener : View.OnClickListener = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                //Toast.makeText(this@MainActivity, "Вы нажали поиск!", Toast.LENGTH_SHORT).show()
                val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }
        }
        searchButton.setOnClickListener(buttonClickListener)

        val mediaButton = findViewById<Button>(R.id.buttonMedia)
        mediaButton.setOnClickListener{
            //Toast.makeText(this@MainActivity, "Вы нажали на медиатеку!", Toast.LENGTH_SHORT).show()
            val mediaLibraryIntent = Intent(this@MainActivity, MediaLibrary::class.java)
            startActivity(mediaLibraryIntent)
        }
        val settingsButton = findViewById<Button>(R.id.buttonSettings)
        settingsButton.setOnClickListener{
            //Toast.makeText(this@MainActivity, "Вы нажали на настройки!", Toast.LENGTH_SHORT).show()
            val settingsIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}