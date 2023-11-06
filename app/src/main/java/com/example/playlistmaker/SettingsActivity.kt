package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backArrow = findViewById<ImageView>(R.id.settings_back_arrow)
        backArrow.setOnClickListener{
            // нужен ли тут интент? ведь я тогда создаю новый активити на стеке
            // а мне это не надо, мне просто надо завершить текущее взаимодействие
            // для этого просто раскручиваю стек на 1 шаг

            //val backIntent = Intent(this, MainActivity::class.java)
            //startActivity(backIntent)
            this.finish()
        }
    }
}