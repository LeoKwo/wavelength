package com.example.wavelength

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Notification.Action
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.CommonDataKinds.Im
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.wavelength.databinding.ActivityPlayerBinding
import com.example.wavelength.model.Song
import com.example.wavelength.retrofit.RetrofitInstance
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.lang.Math.abs


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
private lateinit var streamURL: String

class PlayerActivity : AppCompatActivity() {

    private lateinit var runnable: Runnable
    private var regularAlbumArt: Boolean = false


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)

        regularAlbumArt = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("albumArt", false)

        setContentView(R.layout.activity_player)

        val tvSongTitle = findViewById<TextView>(R.id.tvSongTitle)
        val tvSongAlbum = findViewById<TextView>(R.id.tvSongAlbum)
        val tvSongArtist = findViewById<TextView>(R.id.tvSongArtist)
        val ivPlay = findViewById<ImageView>(R.id.ivPlay)
        val ivBG = findViewById<ImageView>(R.id.ivBG)
        val ivAlbumArt = findViewById<ImageView>(R.id.ivAlbumArt)
        val ivAlbumArtOverlay = findViewById<ImageView>(R.id.ivAlbumArtOverlay)
        val ivFav = findViewById<ImageView>(R.id.ivFav)
        val ivAdd = findViewById<ImageView>(R.id.ivAdd)
        val sbSong = findViewById<SeekBar>(R.id.sbSong)
        val clPlayer = findViewById<ConstraintLayout>(R.id.clPlayer)


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
//        var streamURL = ""
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
                if (!regularAlbumArt) {
                    ivAlbumArt.animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
                    ivAlbumArtOverlay.animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
                }

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
        if (regularAlbumArt) {
            Glide.with(this)
                .load(albumURL)
                .transition(withCrossFade())
                .into(ivAlbumArt)
            ivAlbumArtOverlay.visibility = View.INVISIBLE
        } else {
            Glide.with(this)
                .load(albumURL)
                .transition(withCrossFade())
                .circleCrop()
                .into(ivAlbumArt)
            ivAlbumArtOverlay.visibility = View.VISIBLE
        }

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
                            song.isFavorite = false
                            ivFav.setImageResource(R.drawable.ic_heart_outline)
                            ivFav.setColorFilter(ContextCompat.getColor(this@PlayerActivity, R.color.dark_blue))
                        } else {
                            song.isFavorite = true
                            ivFav.setImageResource(R.drawable.ic_heart)
                            ivFav.setColorFilter(ContextCompat.getColor(this@PlayerActivity, R.color.orange))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.i("post-error", e.message.toString())
            }
        }

        // edit button onclick
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

        // set background with blur
        Glide.with(this)
            .load(albumURL)
            .transition(withCrossFade())
            .centerCrop()
            .into(ivBG)
        ivBG.setRenderEffect(RenderEffect.createBlurEffect(400F, 400F, Shader.TileMode.MIRROR))

        // hide action bar and status bar
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Swipe down to go back
        clPlayer.setOnTouchListener(object : OnTouchListener {
            private val gestureDetector: GestureDetector
            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                return gestureDetector.onTouchEvent(motionEvent)
            }
            private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
                private val SWIPE_THRESHOLD: Int = 100
                private val SWIPE_VELOCITY_THRESHOLD: Int = 100
                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    onClick()
                    return super.onSingleTapUp(e)
                }
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    onDoubleClick()
                    return super.onDoubleTap(e)
                }
                override fun onLongPress(e: MotionEvent) {
                    onLongClick()
                    super.onLongPress(e)
                }
                override fun onFling(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    try {
                        val diffY = e2.y - e1.y
                        val diffX = e2.x - e1.x
                        if (abs(diffX) > abs(diffY)) {
                            if (abs(diffX) > SWIPE_THRESHOLD && abs(
                                    velocityX
                                ) > SWIPE_VELOCITY_THRESHOLD
                            ) {
                                if (diffX > 0) {
                                    onSwipeRight()
                                }
                                else {
                                    onSwipeLeft()
                                }
                            }
                        }
                        else {
                            if (abs(diffY) > SWIPE_THRESHOLD && abs(
                                    velocityY
                                ) > SWIPE_VELOCITY_THRESHOLD
                            ) {
                                if (diffY < 0) {
                                    onSwipeUp()
                                }
                                else {
                                    player.stop()
                                    onSwipeDown()
                                }
                            }
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    return false
                }
            }
            open fun onSwipeRight() {}
            open fun onSwipeLeft() {
//                player.stop()
//                onBackPressed()
//                overridePendingTransition(R.anim.comming_in, R.anim.comming_out)
            }
            open fun onSwipeUp() {}
            open fun onSwipeDown() {
                player.stop()
                onBackPressed()
                overridePendingTransition(R.anim.comming_in, R.anim.comming_out)
            }
            private fun onClick() {}
            private fun onDoubleClick() {}
            private fun onLongClick() {}
            init {
                gestureDetector = GestureDetector(this@PlayerActivity, GestureListener())
            }
        })

//            if (motionEvent.action == ACTION_DOWN) {
//                player.stop()
//                onBackPressed()
//                overridePendingTransition(R.anim.comming_in, R.anim.comming_out)
//            }
//            false

    }

    // add back button
//    override fun onSupportNavigateUp(): Boolean {
//        player.stop()
//        onBackPressed()
//        overridePendingTransition(R.anim.comming_in, R.anim.comming_out)
//        return true
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        player.stop()
    }
}

