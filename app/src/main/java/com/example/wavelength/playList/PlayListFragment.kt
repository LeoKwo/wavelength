package com.example.wavelength.playList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavelength.adapter.MusicAdapter
import com.example.wavelength.adapter.PlayListAdapter
import com.example.wavelength.databinding.FragmentPlaylistBinding
import com.example.wavelength.model.PlayList
import com.example.wavelength.model.Song
import com.example.wavelength.navigateToPlayerActivity
import com.example.wavelength.retrofit.RetrofitInstance
import retrofit2.HttpException
import java.io.IOException

class PlayListFragment: Fragment() {
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var playListAdapter: PlayListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentPlaylistBinding.inflate(layoutInflater)

        initRecyclerView()

//        playListAdapter.onPlayListClickListener= { playList ->
////            Toast.makeText(activity, "${song.songName} by ${song.artistName}", Toast.LENGTH_SHORT).show()
////            currentSong = song
//            activity?.let { navigateToPlayerActivity(it, playList) }
//        }

        getAllPlayLists()

        return binding.root
    }

    private fun initRecyclerView() = binding.rvPlayList.apply {
        playListAdapter = PlayListAdapter()
        adapter = playListAdapter
        layoutManager = LinearLayoutManager(activity)
    }

    override fun onResume() {
        getAllPlayLists()
        super.onResume()
    }

    private fun getAllPlayLists() {
        lifecycleScope.launchWhenCreated {
            val res = try {
                RetrofitInstance.api.getPlayLists()
            } catch(e: IOException) {
                Toast.makeText(activity, "io error: ${e.message}", Toast.LENGTH_LONG).show()
                return@launchWhenCreated
            } catch(e: HttpException) {
                Toast.makeText(activity, "http error", Toast.LENGTH_LONG).show()
                return@launchWhenCreated
            }
            if (res.isSuccessful && res.body() != null) { // 200 status code
                playListAdapter.playLists = res.body()!!
                Log.i("playlists", res.body().toString())
            } else {
                Toast.makeText(activity, "response error", Toast.LENGTH_LONG).show()
            }
        }
    }
}