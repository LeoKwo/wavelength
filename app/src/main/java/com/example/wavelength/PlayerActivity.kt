package com.example.wavelength

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.CircleCropTransformation
import com.example.wavelength.databinding.ActivityPlayerBinding
import com.example.wavelength.model.Song
import java.io.IOException


private const val SONG_KEY = "song"

fun navigateToPlayerActivity(context: Context, song: Song)  {
    val intent = Intent(context, PlayerActivity::class.java)
    val bundle = Bundle()
    bundle.putParcelable(SONG_KEY, song)
    intent.putExtras(bundle)
    context.startActivity(intent)
}

lateinit var binding: ActivityPlayerBinding
lateinit var player: MediaPlayer

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_player)

        val tvSongTitle = findViewById<TextView>(R.id.tvSongTitle)
        val tvSongAlbum = findViewById<TextView>(R.id.tvSongAlbum)
        val tvSongArtist = findViewById<TextView>(R.id.tvSongArtist)
        val ivPlay = findViewById<ImageView>(R.id.ivPlay)
        val ivAlbumArt = findViewById<ImageView>(R.id.ivAlbumArt)

        var playing = false

        player = MediaPlayer()

        val launchIntent = intent
        val song: Song? = launchIntent.extras?.getParcelable<Song>(SONG_KEY)

        // test player
        var streamURL = ""
        var albumURL = ""
        if (song != null) {
            // init album art
            albumURL = "https://musiclibrary.nyc3.cdn.digitaloceanspaces.com/${song.songName}.jpeg"

            // test player
            streamURL = "https://musiclibrary.nyc3.cdn.digitaloceanspaces.com/${song.songName}.mp3"
            tvSongTitle.text = song.songName
            tvSongAlbum.text = song.albumName
            tvSongArtist.text = song.artistName
        }

        Log.i("StreamURL", streamURL)

        try {
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            player.setDataSource(streamURL)
            player.prepare()
        } catch (e: IOException) {
            Log.i("Set streamURL error", e.message.toString())
        }

        ivPlay.setOnClickListener {
            if (!playing) {
                ivPlay.setImageResource(R.drawable.ic_pause)
                player.start()
                playing = true
            } else {
                ivPlay.setImageResource(R.drawable.ic_play)
                player.pause()
                playing = false
            }
            Log.i("status", player.isPlaying.toString())
            Toast.makeText(this, "Now playing: ${song?.songName} by ${song?.artistName}", Toast.LENGTH_SHORT).show()
        }


        // set album art
//        https://musiclibrary.nyc3.cdn.digitaloceanspaces.com/Too%20Good%20At%20Goodbyes.jpeg
//        ivAlbumArt.setImageResource (albumURL)
        ivAlbumArt.load(albumURL) {
            crossfade(true)
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