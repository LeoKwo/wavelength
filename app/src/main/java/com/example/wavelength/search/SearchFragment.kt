package com.example.wavelength.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavelength.R
import com.example.wavelength.adapter.MusicAdapter
import com.example.wavelength.adapter.PlayListAdapter
import com.example.wavelength.databinding.FragmentPlaylistBinding
import com.example.wavelength.databinding.FragmentSearchBinding
import com.example.wavelength.navigateToPlayerActivity
import com.example.wavelength.retrofit.RetrofitInstance
import com.example.wavelength.songList.SongListFragment
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import retrofit2.HttpException
import java.io.IOException


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var musicAdapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (savedInstanceState == null){
//            supportFragmentManager.beginTransaction().replace(R.id.flContainer, Task3MasterFragment()).commit()
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentSearchBinding.inflate(layoutInflater)

        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                getSearchResult(query)
//                musicAdapter.onSongClickListener = { song ->
//                    Toast.makeText(activity, "${song.songName} by ${song.artistName}", Toast.LENGTH_SHORT).show()
////                    currentSong = song
//                    activity?.let { navigateToPlayerActivity(it, song) }
//                }
                initRecyclerView()
                return false
            }
        })

        // wasabeef animation
        binding.rvSearchResultList.itemAnimator = SlideInUpAnimator(OvershootInterpolator(1f))

        return binding.root
    }

    private fun initRecyclerView() = binding.rvSearchResultList.apply {
        musicAdapter = MusicAdapter()
        adapter = musicAdapter
        layoutManager = LinearLayoutManager(activity)
    }

    private fun getSearchResult(query: String) {
        lifecycleScope.launchWhenCreated {
            binding.pbMusicLibrary.isVisible = true
            val res = try {
                RetrofitInstance.api.searchSong(query)
            } catch(e: IOException) {
                Toast.makeText(activity, "io error: ${e.message}", Toast.LENGTH_LONG).show()
                return@launchWhenCreated
            } catch(e: HttpException) {
                Toast.makeText(activity, "http error", Toast.LENGTH_LONG).show()
                return@launchWhenCreated
            }
            if (res.isSuccessful && res.body() != null) { // 200 status code
                musicAdapter.songs = res.body()!!
//                Log.i("getAllSongs", res.body().toString())

                musicAdapter.onSongClickListener = { song ->
                    Toast.makeText(activity, "${song.songName} by ${song.artistName}", Toast.LENGTH_SHORT).show()
//                    currentSong = song
                    activity?.let { navigateToPlayerActivity(it, song) }
                }

            } else {
                Toast.makeText(activity, "response error", Toast.LENGTH_LONG).show()
            }
            binding.pbMusicLibrary.isVisible = false
        }
    }
}

