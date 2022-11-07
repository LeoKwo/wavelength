package com.example.wavelength

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavelength.adapter.MusicAdapter
import com.example.wavelength.databinding.ActivityPlayListBinding

import com.example.wavelength.model.PlayList
import com.example.wavelength.model.Song
import com.example.wavelength.retrofit.RetrofitInstance
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import retrofit2.HttpException
import java.io.IOException
import kotlin.collections.ArrayList

private const val PLAYLIST_KEY = "playlist"
//private const val SONGS_KEY = "songs"
//private const val NAME_KEY = "name"

//fun navigateToPlayListActivity(context: Context, playList: PlayList)  {
//    val intent = Intent(context, PlayListActivity::class.java)
//    val bundle = Bundle()
//    bundle.putParcelable(PLAYLIST_KEY, playList)
//    intent.putExtras(bundle)
//    context.startActivity(intent)
//}
fun navigateToPlayListActivity(context: Context, playList: PlayList)  {
    val intent = Intent(context, PlayListActivity::class.java)
    val bundle = Bundle()
    Log.i("bug?", playList.songs.toString())
    bundle.putParcelable(PLAYLIST_KEY, playList)
//    bundle.putParcelable(SONGS_KEY, playList.songs[0])
//    val list: List<Song> = playList.songs
//    val songs: ArrayList<Song> = ArrayList<Song>()
//    songs.addAll(list)
//    bundle.putSerializable(SONGS_KEY, songs)
//    Log.i("bug?", (list as java.io.Serializable).toString())
    intent.putExtras(bundle)
    context.startActivity(intent)
}

lateinit var bindingPlayListBinding: ActivityPlayListBinding
private lateinit var musicAdapter: MusicAdapter

class PlayListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingPlayListBinding = ActivityPlayListBinding.inflate(layoutInflater)
        setContentView(bindingPlayListBinding.root)

        val launchIntent = intent
//        launchIntent.extras?.getParcelable<PlayList>(PLAYLIST_KEY)?.let { Log.i("Bug_here",
//            it.toString()
//        ) }
//        launchIntent.extras?.getParcelable<Song>(SONGS_KEY)?.let { Log.i("debugLeo", it) }
//        Log.i("Bug_here", launchIntent.extras?.getParcelable<Song>(SONGS_KEY)?.)

//        val playListName: String? = launchIntent.extras?.getString(NAME_KEY)
//        val songs: ArrayList<Parcelable>? = launchIntent.extras?.getParcelableArrayList(SONGS_KEY)
//        if (playListName != null) {
//            Log.i("debugLeo", playListName)
//            Log.i("debugLeo", songs.toString())
//        }

        val playList: PlayList? = launchIntent.extras?.getParcelable<PlayList>(PLAYLIST_KEY)


        // GET SONGS
        if (playList != null) {
            Log.i("bug??????", playList.name)
            getSongs(playList)
        } else {
            Toast.makeText(this@PlayListActivity, "Error getting playlist data", Toast.LENGTH_SHORT).show()
        }

        // INIT RECYCLERVIEW
        initRecyclerView()

        // SETUP SUPPORTACTION BAR
//        if (playList?.id == "1") title = "Favorites"
        title = playList?.name

        supportActionBar?.apply {
            elevation = 0F
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Wasabeef animation
        bindingPlayListBinding.rvSongs.itemAnimator = SlideInUpAnimator(OvershootInterpolator(1f))
    }

    // REFRESH PAGE ON BACK BUTTON PRESSED
    override fun onRestart() {
        super.onRestart()
        val launchIntent = intent
        val playList: PlayList? = launchIntent.extras?.getParcelable<PlayList>(PLAYLIST_KEY)
        if (playList != null) getSongs(playList)
    }

    private fun initRecyclerView() = bindingPlayListBinding.rvSongs.apply {
        musicAdapter = MusicAdapter()
        adapter = musicAdapter
        layoutManager = LinearLayoutManager(this@PlayListActivity)
    }

    private fun getSongs(playList: PlayList) {
        Log.i("bug??", playList.name)
        lifecycleScope.launchWhenCreated {
            bindingPlayListBinding.pbPlayList.isVisible = true
            if (playList.id == "1") { // get favorites
                val res = try {
                    RetrofitInstance.api.getFavSongs()
                } catch(e: IOException) {
                    Toast.makeText(this@PlayListActivity, "io error: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@launchWhenCreated
                } catch(e: HttpException) {
                    Toast.makeText(this@PlayListActivity, "http error", Toast.LENGTH_SHORT).show()
                    return@launchWhenCreated
                }
                if (res.isSuccessful && res.body() != null) { // 200 status code
                    musicAdapter.songs = res.body()!!
                    Log.i("getFavorites", res.body().toString())
                    musicAdapter.onSongClickListener = { song ->
                        Toast.makeText(this@PlayListActivity, "${song.songName} by ${song.artistName}", Toast.LENGTH_SHORT).show()
//                    currentSong = song
                        navigateToPlayerActivity(this@PlayListActivity, song)
                    }
                } else {
                    Toast.makeText(this@PlayListActivity, "response error", Toast.LENGTH_SHORT).show()
                }
            } else { // get regular playlist
                val res = try {
                    RetrofitInstance.api.getPlayList(playList.id)
                } catch(e: IOException) {
                    Toast.makeText(this@PlayListActivity, "io error: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@launchWhenCreated
                } catch(e: HttpException) {
                    Toast.makeText(this@PlayListActivity, "http error", Toast.LENGTH_SHORT).show()
                    return@launchWhenCreated
                }
                if (res.isSuccessful && res.body() != null) { // 200 status code
                    val songs = try {
                        RetrofitInstance.api.getSongs()
                    } catch(e: IOException) {
                        Toast.makeText(this@PlayListActivity, "io error: ${e.message}", Toast.LENGTH_SHORT).show()
                        return@launchWhenCreated
                    } catch(e: HttpException) {
                        Toast.makeText(this@PlayListActivity, "http error", Toast.LENGTH_SHORT).show()
                        return@launchWhenCreated
                    }
                    val songsInPlayList: MutableList<Song> = mutableListOf()
                    if (songs.isSuccessful && songs.body() != null) {
                        for (s in songs.body()!!) {
                            if (playList.songs.contains(s.id)) songsInPlayList.add(s)
                        }
                        musicAdapter.songs = songsInPlayList

                        musicAdapter.onSongClickListener = { song ->
                            Toast.makeText(
                                this@PlayListActivity,
                                "${song.songName} by ${song.artistName}",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateToPlayerActivity(this@PlayListActivity, song)
                        }
                    }
                } else {
                    Toast.makeText(this@PlayListActivity, "response error", Toast.LENGTH_SHORT).show()
                }
            }


//            val res = try {
//                if (playList.id == "1") {
//                    RetrofitInstance.api.getFavSongs()
//                } else {
//                    // TODO: Remove this. This is placeholder for now
//                    RetrofitInstance.api.searchSong("too")
////                    RetrofitInstance.api.getFavSongs()
////                    RetrofitInstance.api.getPlayList(playList.id)
//                }
////                RetrofitInstance.api.getFavSongs()
////                RetrofitInstance.api.searchSong(query)
//            } catch(e: IOException) {
//                Toast.makeText(this@PlayListActivity, "io error: ${e.message}", Toast.LENGTH_SHORT).show()
//                return@launchWhenCreated
//            } catch(e: HttpException) {
//                Toast.makeText(this@PlayListActivity, "http error", Toast.LENGTH_SHORT).show()
//                return@launchWhenCreated
//            }
//            if (res.isSuccessful && res.body() != null) { // 200 status code
//                musicAdapter.songs = res.body()!!
//                Log.i("getFavorites", res.body().toString())
//
//                musicAdapter.onSongClickListener = { song ->
//                    Toast.makeText(this@PlayListActivity, "${song.songName} by ${song.artistName}", Toast.LENGTH_SHORT).show()
////                    currentSong = song
//                    navigateToPlayerActivity(this@PlayListActivity, song)
//                }
//
//            } else {
//                Toast.makeText(this@PlayListActivity, "response error", Toast.LENGTH_SHORT).show()
//            }
            bindingPlayListBinding.pbPlayList.isVisible = false

        }
    }

    // add back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}