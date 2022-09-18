package com.example.wavelength

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import coil.load
import com.example.wavelength.databinding.ActivityPlayerBinding
import com.example.wavelength.model.Song
import com.example.wavelength.retrofit.RetrofitInstance
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
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
        val ivFav = findViewById<ImageView>(R.id.ivFav)

        supportActionBar?.apply {
            title = "Now playing"
            elevation = 0F
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        player = MediaPlayer()

        // launch activity with intent
        val launchIntent = intent
        val song: Song? = launchIntent.extras?.getParcelable<Song>(SONG_KEY)

        // init
        var streamURL = ""
        var albumURL = ""
        if (song != null) {
            // init album art
            albumURL = "https://musiclibrary.nyc3.cdn.digitaloceanspaces.com/${song.songName}.jpeg"

            // init player
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
            if (!player.isPlaying) {
                Toast.makeText(this, "Now playing: ${song?.songName} by ${song?.artistName}", Toast.LENGTH_SHORT).show()
                ivPlay.setImageResource(R.drawable.ic_pause)
                player.start()
            } else {
                ivPlay.setImageResource(R.drawable.ic_play)
                player.pause()
            }
            Log.i("status", player.isPlaying.toString())
        }

        // set album art
        ivAlbumArt.load(albumURL) {
            crossfade(true)
        }

        // render fav button
        if (song?.isFavorite == true) {
            ivFav.setImageResource(R.drawable.ic_heart)
            ivFav.setColorFilter(ContextCompat.getColor(this, R.color.orange))
        }

        suspend fun favBtnCallback(song: Song) = coroutineScope {
            async { RetrofitInstance.api.favSong(song.id, !song.isFavorite) }
        }.await()

        ivFav.setOnClickListener{
            try {
                runBlocking {
                    if (song != null) {
                        favBtnCallback(song)
                        if (song.isFavorite) {
                            ivFav.setImageResource(R.drawable.ic_heart_outline)
                            ivFav.setColorFilter(ContextCompat.getColor(this@PlayerActivity, R.color.dark_gray))
                        } else {
                            ivFav.setImageResource(R.drawable.ic_heart)
                            ivFav.setColorFilter(ContextCompat.getColor(this@PlayerActivity, R.color.orange))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.i("post-error", e.message.toString())
            }
        }

        // load background image

    }

    // add back button
    override fun onSupportNavigateUp(): Boolean {
        player.stop()
        onBackPressed()
        return true
    }
}
