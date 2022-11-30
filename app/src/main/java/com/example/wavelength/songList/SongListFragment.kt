package com.example.wavelength.songList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
//import androidx.lifecycle.LifecycleOwner
//import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.wavelength.adapter.MusicAdapter
import com.example.wavelength.databinding.FragmentSongListBinding
import com.example.wavelength.model.Song
import com.example.wavelength.navigateToPlayerActivity
import com.example.wavelength.retrofit.RetrofitInstance
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import okhttp3.internal.notify
import retrofit2.HttpException
import java.io.IOException

class SongListFragment : Fragment()  {

    private lateinit var binding: FragmentSongListBinding

    private lateinit var musicAdapter: MusicAdapter

    private lateinit var currentSong: Song

    private var sortedBy = "none"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        binding = FragmentSongListBinding.inflate(layoutInflater)

        sortedBy = PreferenceManager.getDefaultSharedPreferences(context).getString("librarySorting", "none").toString()

        createRecyclerView()

        // Wasabeef animations item animation
        binding.rvMusicLibrary.itemAnimator = SlideInUpAnimator(OvershootInterpolator(1f))

        // swipe to refresh
        binding.srlSongList.setOnRefreshListener {
            createRecyclerView()
            binding.srlSongList.isRefreshing = false
        }

        return binding.root
    }

    private fun createRecyclerView() {
        initRecyclerView()
        musicAdapter.onSongClickListener = { song ->
            Toast.makeText(
                activity,
                "${song.songName} by ${song.artistName}",
                Toast.LENGTH_SHORT
            ).show()
            currentSong = song
            activity?.let { navigateToPlayerActivity(it, currentSong) }
        }
        getAllSongs()
    }

    // refresh on back button pressed
    override fun onResume() {
        getAllSongs()
        super.onResume()
    }

    private fun getAllSongs() {
        lifecycleScope.launchWhenCreated {
            binding.pbMusicLibrary.isVisible = true
            val res = try {
                RetrofitInstance.api.getSongs()
            } catch(e: IOException) {
                Toast.makeText(activity, "io error: ${e.message}", Toast.LENGTH_SHORT).show()
                return@launchWhenCreated
            } catch(e: HttpException) {
                Toast.makeText(activity, "http error", Toast.LENGTH_SHORT).show()
                return@launchWhenCreated
            }
            if (res.isSuccessful && res.body() != null) { // 200 status code
                musicAdapter.songs = res.body()!!
//                Log.i("getAllSongs", res.body().toString())
//                Log.i("sortedBy", sortedBy)
                when (sortedBy) {
                    "album" -> {
                        musicAdapter.sortByAlbum()
                        Toast.makeText(context, "Sorted by album", Toast.LENGTH_SHORT).show()
                    }
                    "title" -> {
                        musicAdapter.sortByTitle()
                        Toast.makeText(context, "Sorted by title", Toast.LENGTH_SHORT).show()
                    }
                    "artist" -> {
                        musicAdapter.sortByArtist()
                        Toast.makeText(context, "Sorted by artist", Toast.LENGTH_SHORT).show()
                    }
                }
                binding.rvMusicLibrary.smoothScrollToPosition(0)
            } else {
                Toast.makeText(activity, "response error", Toast.LENGTH_SHORT).show()
            }
            binding.pbMusicLibrary.isVisible = false
        }
    }



    private fun initRecyclerView() = binding.rvMusicLibrary.apply {
        musicAdapter = MusicAdapter()
        adapter = musicAdapter
        layoutManager = LinearLayoutManager(activity)
    }
}