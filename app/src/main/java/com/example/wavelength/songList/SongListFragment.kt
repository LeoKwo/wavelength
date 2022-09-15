package com.example.wavelength.songList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.LifecycleOwner
//import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavelength.adapter.MusicAdapter
import com.example.wavelength.databinding.FragmentSongListBinding
import com.example.wavelength.model.Song
import com.example.wavelength.navigateToPlayerActivity
import com.example.wavelength.retrofit.RetrofitInstance
import retrofit2.HttpException
import java.io.IOException

class SongListFragment : Fragment()  {
//    private lateinit var binding: FragmentSongListBinding
//
//    private lateinit var musicAdapter: MusicAdapter
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View? {
//        super.onCreate(savedInstanceState)
//        binding = FragmentSongListBinding.inflate(layoutInflater)
//        initRecyclerView()
//        lifecycleScope.launchWhenCreated {
//            binding.pbMusicLibrary.isVisible = true
//            val res = try {
//                RetrofitInstance.api.getSongs()
//            } catch(e: IOException) {
//                Toast.makeText(activity, "io error", Toast.LENGTH_LONG).show()
//                return@launchWhenCreated
//            } catch(e: HttpException) {
//                Toast.makeText(activity, "http error", Toast.LENGTH_LONG).show()
//                return@launchWhenCreated
//            }
//            if (res.isSuccessful && res.body() != null) { // 200 status code
//                musicAdapter.todos = res.body()!!
//            } else {
//                Toast.makeText(activity, "response error", Toast.LENGTH_LONG).show()
//            }
//            binding.pbMusicLibrary.isVisible = false
//        }
//        return binding.root
//    }
//
//    private fun initRecyclerView() = binding.rvMusicLibrary.apply {
//        musicAdapter = MusicAdapter()
//        adapter = musicAdapter
//        layoutManager = LinearLayoutManager(activity)
//    }

    private lateinit var binding: FragmentSongListBinding

    private lateinit var musicAdapter: MusicAdapter

    private lateinit var currentSong: Song

//    private lateinit var clSongItem: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        binding = FragmentSongListBinding.inflate(layoutInflater)

        initRecyclerView()

        musicAdapter.onSongClickListener = { song ->
                Toast.makeText(activity, "Now playing: ${song.songName} by ${song.artistName}", Toast.LENGTH_SHORT).show()
////                val title = song.title
////                val artist = song.artist
//            tvSongInfo.text = root.context.getString(R.string.song_info_format, song.title, song.artist)
////                tvInfoArtistName.text = song.artist
//            clSongInfo.isInvisible = false

            currentSong = song
            activity?.let { navigateToPlayerActivity(it, currentSong) }
        }

//        clSongItem

//        navigateToPlayerActivity(activity, currentSong)
//        clSongInfo.setOnClickListener {
//            navigateToMainActivity(this@SongListActivity, currentlyPlaying)
//        }

        lifecycleScope.launchWhenCreated {
            binding.pbMusicLibrary.isVisible = true
            val res = try {
                RetrofitInstance.api.getSongs()
            } catch(e: IOException) {
                Toast.makeText(activity, "io error: ${e.message}", Toast.LENGTH_LONG).show()
                return@launchWhenCreated
            } catch(e: HttpException) {
                Toast.makeText(activity, "http error", Toast.LENGTH_LONG).show()
                return@launchWhenCreated
            }
            if (res.isSuccessful && res.body() != null) { // 200 status code
                musicAdapter.songs = res.body()!!
            } else {
                Toast.makeText(activity, "response error", Toast.LENGTH_LONG).show()
            }
            binding.pbMusicLibrary.isVisible = false
        }
        return binding.root
    }

    private fun initRecyclerView() = binding.rvMusicLibrary.apply {
        musicAdapter = MusicAdapter()
        adapter = musicAdapter
        layoutManager = LinearLayoutManager(activity)
    }
}