package com.example.wavelength

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.wavelength.databinding.ActivityPlayerBinding
import com.example.wavelength.model.Song

private const val SONG_KEY = "song"

fun navigateToPlayerActivity(context: Context, song: Song)  {
    val intent = Intent(context, PlayerActivity::class.java)
    val bundle = Bundle()
    bundle.putParcelable(SONG_KEY, song)
    intent.putExtras(bundle)
    context.startActivity(intent)
}

lateinit var binding: ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_player)

        val tvSongTitle = findViewById<TextView>(R.id.tvSongTitle)
        val tvSongAlbum = findViewById<TextView>(R.id.tvSongAlbum)
        val tvSongArtist = findViewById<TextView>(R.id.tvSongArtist)

        val launchIntent = intent
        val song: Song? = launchIntent.extras?.getParcelable<Song>(SONG_KEY)
//        Log.i("playerIntent", song?.songName.toString())

        if (song != null) {
            tvSongTitle.text = song.songName
            tvSongAlbum.text = song.albumName
            tvSongArtist.text = song.artistName
        }

        // add back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // change actionbar title
        supportActionBar?.title = "Now playing"

    }

    // add back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}