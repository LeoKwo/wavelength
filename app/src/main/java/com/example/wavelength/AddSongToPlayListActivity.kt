package com.example.wavelength

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavelength.adapter.AddToPlayListAdapter
import com.example.wavelength.adapter.MusicAdapter
import com.example.wavelength.adapter.RemoveFromPlayListAdapter
import com.example.wavelength.databinding.ActivityAddSongToPlayListBinding
import com.example.wavelength.model.PlayList
import com.example.wavelength.model.Song
import com.example.wavelength.requestBody.UpdatePlayListBody
import com.example.wavelength.retrofit.RetrofitInstance
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import retrofit2.HttpException
import java.io.IOException

private const val SONG_KEY = "SONG KEY"

private lateinit var activityAddSongToPlayListBinding: ActivityAddSongToPlayListBinding
private lateinit var addToPlayListAdapter: AddToPlayListAdapter
private lateinit var removeFromPlayListAdapter: RemoveFromPlayListAdapter
private lateinit var addToPlayListAdapterOther: AddToPlayListAdapter
private lateinit var song: Song

fun navigateToAddSongToPlayListActivity(context: Context, song: Song)  {
    val intent = Intent(context, AddSongToPlayListActivity::class.java)
    val bundle = Bundle()
    bundle.putParcelable(SONG_KEY, song)
    intent.putExtras(bundle)
    context.startActivity(intent)
}

class AddSongToPlayListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddSongToPlayListBinding = ActivityAddSongToPlayListBinding.inflate(layoutInflater)
        setContentView(activityAddSongToPlayListBinding.root)
//        setContentView(R.layout.activity_add_song_to_play_list)

        supportActionBar?.apply {
            title = "Back"
            elevation = 0F
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val launchIntent = intent
        launchIntent.extras?.getParcelable<Song>(SONG_KEY).let { it ->
            if (it != null) {
                song = it
            }
        }

        with(activityAddSongToPlayListBinding) {
            // set animation
            rvNewPlayList.itemAnimator = SlideInUpAnimator(OvershootInterpolator(1f))
            rvExistingPlayList.itemAnimator = SlideInUpAnimator(OvershootInterpolator(1f))
            // create recycler view
            createRecyclerView()
        }

        addToPlayListAdapter.onPlayListClickListener = { playList ->
            lifecycleScope.launchWhenCreated {
                var list = playList.songs as MutableList
                song?.id?.let { list.add(it) }
                val res = try {
                    RetrofitInstance.api.updatePlayList(
                        playList.id,
                        UpdatePlayListBody(playList.name, playList.artUrl, list)
                    )
                } catch (e: IOException) {
                    Toast.makeText(
                        this@AddSongToPlayListActivity,
                        "io error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launchWhenCreated
                } catch (e: HttpException) {
                    Toast.makeText(this@AddSongToPlayListActivity, "http error", Toast.LENGTH_SHORT)
                        .show()
                    return@launchWhenCreated
                }
                if (res.isSuccessful) { // 200 status code
                    Toast.makeText(
                        this@AddSongToPlayListActivity,
                        "Added ${song?.songName} to ${playList.name}",
                        Toast.LENGTH_SHORT
                    ).show()
//                    createRecyclerView()
//                    this@AddSongToPlayListActivity.finish()
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@AddSongToPlayListActivity,
                        "response error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        removeFromPlayListAdapter.onPlayListClickListener = { playList ->
            lifecycleScope.launchWhenCreated {
                var list = playList.songs as MutableList
                song?.id?.let { list.remove(it) }
                val res = try {
                    RetrofitInstance.api.updatePlayList(
                        playList.id,
                        UpdatePlayListBody(playList.name, playList.artUrl, list)
                    )
                } catch (e: IOException) {
                    Toast.makeText(
                        this@AddSongToPlayListActivity,
                        "io error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launchWhenCreated
                } catch (e: HttpException) {
                    Toast.makeText(this@AddSongToPlayListActivity, "http error", Toast.LENGTH_SHORT)
                        .show()
                    return@launchWhenCreated
                }
                if (res.isSuccessful) { // 200 status code
                    Toast.makeText(
                        this@AddSongToPlayListActivity,
                        "Removed ${song?.songName} from ${playList.name}",
                        Toast.LENGTH_SHORT
                    ).show()
//                    createRecyclerView()
//                    this@AddSongToPlayListActivity.finish()
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@AddSongToPlayListActivity,
                        "response error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun createRecyclerView() {
        getAllPlaylists()
        initRecyclerView()
//        musicAdapter.onSongClickListener = { song ->
//            Toast.makeText(activity, "${song.songName} by ${song.artistName}", Toast.LENGTH_SHORT).show()
//            currentSong = song
//            activity?.let { navigateToPlayerActivity(it, currentSong) }
//        }
        initRecyclerViewOther()
    }

    override fun onResume() {
        getAllPlaylists()
        super.onResume()
    }

    private fun getAllPlaylists() {
        lifecycleScope.launchWhenCreated {
            activityAddSongToPlayListBinding.pbMusicLibrary.isVisible = true
            val res = try {
                RetrofitInstance.api.getPlayLists()
            } catch(e: IOException) {
                Toast.makeText(this@AddSongToPlayListActivity, "io error: ${e.message}", Toast.LENGTH_SHORT).show()
                return@launchWhenCreated
            } catch(e: HttpException) {
                Toast.makeText(this@AddSongToPlayListActivity, "http error", Toast.LENGTH_SHORT).show()
                return@launchWhenCreated
            }
            if (res.isSuccessful && res.body() != null) { // 200 status code
                var playlists = mutableListOf<PlayList>()
                var playlistsAlreadyIn = mutableListOf<PlayList>()
                for (playlist in res.body()!!) {
                    if (::song.isInitialized) {
                        if (playlist.id != "1") {
                            if (!playlist.songs.contains(song.id)) {
                                playlists.add(playlist)
                            } else {
                                playlistsAlreadyIn.add(playlist)
                            }
                        }
                    }
                }
                addToPlayListAdapter.playlists = playlists
                removeFromPlayListAdapter.playlists = playlistsAlreadyIn
//                Log.i("getAllSongs", res.body().toString())
            } else {
                Toast.makeText(this@AddSongToPlayListActivity, "response error", Toast.LENGTH_SHORT).show()
            }
            activityAddSongToPlayListBinding.pbMusicLibrary.isVisible = false
        }
    }

    private fun initRecyclerView() = activityAddSongToPlayListBinding.rvNewPlayList.apply {
        addToPlayListAdapter = AddToPlayListAdapter()
        adapter = addToPlayListAdapter
        layoutManager = LinearLayoutManager(this@AddSongToPlayListActivity)
    }

    private fun initRecyclerViewOther() = activityAddSongToPlayListBinding.rvExistingPlayList.apply {
        removeFromPlayListAdapter = RemoveFromPlayListAdapter()
        adapter = removeFromPlayListAdapter
        layoutManager = LinearLayoutManager(this@AddSongToPlayListActivity)
    }

    // add back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}