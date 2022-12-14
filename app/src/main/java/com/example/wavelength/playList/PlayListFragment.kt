package com.example.wavelength.playList

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavelength.*
import com.example.wavelength.adapter.MusicAdapter
import com.example.wavelength.adapter.PlayListAdapter
import com.example.wavelength.databinding.FragmentPlaylistBinding
import com.example.wavelength.model.PlayList
import com.example.wavelength.model.Song
import com.example.wavelength.retrofit.RetrofitInstance
import com.google.android.material.floatingactionbutton.FloatingActionButton
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import retrofit2.HttpException
import java.io.IOException

class PlayListFragment: Fragment() {
    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var playListAdapter: PlayListAdapter
    private lateinit var btPlayListAdd: FloatingActionButton
    private var editButtonPressed: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentPlaylistBinding.inflate(layoutInflater)

        initRecyclerView()

        playListAdapter.onPlayListClickListener = { playList ->
            activity?.let {
//                Log.i("bug_here", playList.songs[0].toString())
                navigateToPlayListActivity(it, playList)
            }
        }

        playListAdapter.onPlayListEditClickListener = { playList ->
            activity?.let {
                navigateToEditPlayListActivity(it, playList)
            }
        }

        playListAdapter.onPlayListRemoveClickListener = { playList ->
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Do you really wish to delete this playlist?")
            builder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
                lifecycleScope.launchWhenCreated {
                    val res = try {
                        RetrofitInstance.api.removePlayList(playList.id)
                    } catch(e: IOException) {
                        Toast.makeText(activity, "io error: ${e.message}", Toast.LENGTH_SHORT).show()
                        return@launchWhenCreated
                    } catch(e: HttpException) {
                        Toast.makeText(activity, "http error", Toast.LENGTH_SHORT).show()
                        return@launchWhenCreated
                    }
                    // refresh on success
                    if (res.isSuccessful) { // 200 status code
                        getAllPlayLists()
                        Toast.makeText(activity, "Removed ${playList.name} playlist", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(activity, "response error", Toast.LENGTH_SHORT).show()
                    }
                }
            })
            builder.setNegativeButton("Cancel") { _, _ -> }
            builder.show()
        }

        getAllPlayLists()

        // Wasabeef animation
        binding.rvPlayList.itemAnimator = SlideInUpAnimator(OvershootInterpolator(1f))

        // Floating action bar onclick
        btPlayListAdd = binding.btPlayListEdit
        btPlayListAdd.setOnClickListener {
            startActivity(Intent(context, CreatePlayListActivity::class.java))
        }


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
            binding.pbPlayList.isVisible = true
            val res = try {
                RetrofitInstance.api.getPlayLists()
            } catch(e: IOException) {
                Toast.makeText(activity, "io error: ${e.message}", Toast.LENGTH_SHORT).show()
                return@launchWhenCreated
            } catch(e: HttpException) {
                Toast.makeText(activity, "http error", Toast.LENGTH_SHORT).show()
                return@launchWhenCreated
            }
            if (res.isSuccessful && res.body() != null) { // 200 status code
                playListAdapter.playLists = res.body()!!
                Log.i("playlists", res.body().toString())
            } else {
                Toast.makeText(activity, "response error", Toast.LENGTH_SHORT).show()
            }
            binding.pbPlayList.isVisible = false
        }
    }
}