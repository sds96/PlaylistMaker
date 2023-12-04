package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backArrow = findViewById<ImageView>(R.id.settings_back_arrow)
        backArrow.setOnClickListener{
            finish()
        }

        val shareBlock = findViewById<FrameLayout>(R.id.shareFrame)
        shareBlock.setOnClickListener{
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.Share_app_link))
            shareIntent.type = "text/plain"
            startActivity(shareIntent)
        }

        val supportBlock = findViewById<FrameLayout>(R.id.supportFrame)
        supportBlock.setOnClickListener{
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(resources.getString(R.string.developer_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.support_message_title))
            supportIntent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.support_message_text))
            startActivity(supportIntent)
        }

        val licenceBlock = findViewById<FrameLayout>(R.id.licenceFrame)
        licenceBlock.setOnClickListener{
            val licenceIntent = Intent(Intent.ACTION_VIEW)
            licenceIntent.data = Uri.parse(resources.getString(R.string.user_agreement_link))
            startActivity(licenceIntent)
        }
    }
}