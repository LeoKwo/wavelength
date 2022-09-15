package com.example.wavelength.playList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wavelength.databinding.FragmentPlaylistBinding

class PlayListFragment: Fragment() {
    private lateinit var binding: FragmentPlaylistBinding

//    private lateinit var musicAdapter: MusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentPlaylistBinding.inflate(layoutInflater)

//        initRecyclerView()
//        lifecycleScope.launchWhenCreated {
//            binding.pbMusicLibrary.isVisible = true
//            val res = try {
//                RetrofitInstance.api.getSongs()
//            } catch(e: IOException) {
//                Toast.makeText(activity, "io error: ${e.message}", Toast.LENGTH_LONG).show()
//                return@launchWhenCreated
//            } catch(e: HttpException) {
//                Toast.makeText(activity, "http error", Toast.LENGTH_LONG).show()
//                return@launchWhenCreated
//            }
//            if (res.isSuccessful && res.body() != null) { // 200 status code
//                musicAdapter.songs = res.body()!!
//            } else {
//                Toast.makeText(activity, "response error", Toast.LENGTH_LONG).show()
//            }
//            binding.pbMusicLibrary.isVisible = false
//        }
        return binding.root
    }

//    private fun initRecyclerView() = binding.rvMusicLibrary.apply {
//        musicAdapter = MusicAdapter()
//        adapter = musicAdapter
//        layoutManager = LinearLayoutManager(activity)
//    }

}