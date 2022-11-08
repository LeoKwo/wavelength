package com.example.wavelength

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import coil.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.example.wavelength.databinding.ActivityPlayerBinding
import com.example.wavelength.model.Song
import com.example.wavelength.retrofit.RetrofitInstance
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.io.IOException


private const val SONG_KEY = "song"
private var albumIsCircle = true

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

    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_player)

        val tvSongTitle = findViewById<TextView>(R.id.tvSongTitle)
        val tvSongAlbum = findViewById<TextView>(R.id.tvSongAlbum)
        val tvSongArtist = findViewById<TextView>(R.id.tvSongArtist)
        val ivPlay = findViewById<ImageView>(R.id.ivPlay)
        val ivAlbumArt = findViewById<ImageView>(R.id.ivAlbumArt)
        val ivAlbumArtOverlay = findViewById<ImageView>(R.id.ivAlbumArtOverlay)
        val ivFav = findViewById<ImageView>(R.id.ivFav)
        val ivAdd = findViewById<ImageView>(R.id.ivAdd)

        val sbSong = findViewById<SeekBar>(R.id.sbSong)


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

        // init media player for audio files
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

        // play/pause button animation
        ivPlay.setOnClickListener {
            if (!player.isPlaying) {
                ivAlbumArt.animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
                ivAlbumArtOverlay.animation = AnimationUtils.loadAnimation(this, R.anim.rotate)

                ivPlay.setImageResource(R.drawable.ic_pause)
                player.start()
            } else {
                ivAlbumArt.animation = null
                ivAlbumArtOverlay.animation = null
                ivPlay.setImageResource(R.drawable.ic_play)
                player.pause()
            }
            Log.i("status", player.isPlaying.toString())
        }

        // set album art
//        ivAlbumArt.load(albumURL) {
//            albumIsCircle = true
//            crossfade(true)
//            transformations(CircleCropTransformation())
//        }
        Glide.with(this)
            .load(albumURL)
            .transition(withCrossFade())
            .circleCrop()
            .into(ivAlbumArt)
        albumIsCircle = true

        // render fav button
        if (song?.isFavorite == true) {
            ivFav.setImageResource(R.drawable.ic_heart)
            ivFav.setColorFilter(ContextCompat.getColor(this, R.color.orange))
        }

        suspend fun favBtnCallback(song: Song) = coroutineScope {
            async { RetrofitInstance.api.favSong(song.id, !song.isFavorite) }
        }.await()

        // fav button onclick
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

        // long click on album art
        // long click will change album art style (circle or square)
        ivAlbumArt.setOnLongClickListener {
            if (albumIsCircle) {
                // disable animations
                ivAlbumArt.animation = null
                ivAlbumArtOverlay.animation = null

                ivAlbumArtOverlay.visibility = View.INVISIBLE
//                ivAlbumArt.load(albumURL) {
//                    crossfade(true)
//                }
                Glide.with(this)
                    .load(albumURL)
                    .transition(withCrossFade())
                    .into(ivAlbumArt)
                albumIsCircle = false
            } else {
                ivAlbumArtOverlay.visibility = View.VISIBLE
                // re-enable animation
                ivAlbumArt.animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
                ivAlbumArtOverlay.animation = AnimationUtils.loadAnimation(this, R.anim.rotate)

                Glide.with(this)
                    .load(albumURL)
                    .transition(withCrossFade())
                    .circleCrop()
                    .into(ivAlbumArt)
                albumIsCircle = true
            }
            true
        }

        ivAdd.setOnClickListener {
            this?.let { navigateToAddSongToPlayListActivity(it, song!!) }
        }

        // seekbar
        sbSong.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seek: SeekBar,
                                               progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        player.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seek: SeekBar) { }

                override fun onStopTrackingTouch(seek: SeekBar) { }
            }
        )

        // set seekbar progress
        sbSong.max = player.duration
        runnable = Runnable {
            sbSong.progress = player.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 1000)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 1000)
        player.setOnCompletionListener {
            player.pause()
            player.seekTo(0)
            ivAlbumArt.animation = null
            ivAlbumArtOverlay.animation = null
            ivPlay.setImageResource(R.drawable.ic_play)
        }
    }

    // add back button
    override fun onSupportNavigateUp(): Boolean {
        player.stop()
        onBackPressed()
        return true
    }
}
