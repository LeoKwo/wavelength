package com.example.wavelength

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavelength.adapter.MusicAdapter
import com.example.wavelength.databinding.ActivityPlayListBinding

import com.example.wavelength.model.PlayList
import com.example.wavelength.model.Song
import com.example.wavelength.retrofit.RetrofitInstance
import retrofit2.HttpException
import java.io.IOException

private const val PLAYLIST_KEY = "playList"

fun navigateToPlayListActivity(context: Context, playList: PlayList)  {
    val intent = Intent(context, PlayListActivity::class.java)
    val bundle = Bundle()
    bundle.putParcelable(PLAYLIST_KEY, playList)
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
        val playList: PlayList? = launchIntent.extras?.getParcelable<PlayList>(PLAYLIST_KEY)

        // GET SONGS
        if (playList != null) {
            getSongs(playList)
        } else {
            Toast.makeText(this@PlayListActivity, "Error getting playlist data", Toast.LENGTH_SHORT).show()
        }

        // INIT RECYCLERVIEW
        initRecyclerView()

        // SETUP SUPPORTACTION BAR
        if (playList?.id == "1") title = "Favorites"
        supportActionBar?.apply {
            elevation = 0F
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
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
        lifecycleScope.launchWhenCreated {
            bindingPlayListBinding.pbPlayList.isVisible = true
            val res = try {
                if (playList.id == "1") {
//                    Log.i("Reached here!", playList.id)

                    RetrofitInstance.api.getFavSongs()
                } else {
                    // TODO: Remove this. This is placeholder for now
                    RetrofitInstance.api.searchSong("too")
//                    RetrofitInstance.api.getFavSongs()
                }
//                RetrofitInstance.api.getFavSongs()
//                RetrofitInstance.api.searchSong(query)
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
            bindingPlayListBinding.pbPlayList.isVisible = false

        }
    }

    // add back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}