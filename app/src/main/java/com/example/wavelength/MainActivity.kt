package com.example.wavelength

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
//import androidx.lifecycle.LifecycleOwner
//import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavelength.adapter.MusicAdapter
import com.example.wavelength.databinding.ActivityMainBinding
import com.example.wavelength.retrofit.RetrofitInstance
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.HttpException
import java.io.IOException
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

//    private lateinit var binding: ActivityMainBinding
//
//    private lateinit var musicAdapter: MusicAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        initRecyclerView()
//
//        lifecycleScope.launchWhenCreated {
//            binding.pbMusicLibrary.isVisible = true
//            val res = try {
//                RetrofitInstance.api.getSongs()
//            } catch(e: IOException) {
//                Toast.makeText(this@MainActivity, "io error", Toast.LENGTH_LONG).show()
//                return@launchWhenCreated
//            } catch(e: HttpException) {
//                Toast.makeText(this@MainActivity, "http error", Toast.LENGTH_LONG).show()
//                return@launchWhenCreated
//            }
//            if (res.isSuccessful && res.body() != null) { // 200 status code
//                musicAdapter.todos = res.body()!!
//            } else {
//                Toast.makeText(this@MainActivity, "response error", Toast.LENGTH_LONG).show()
//            }
//            binding.pbMusicLibrary.isVisible = false
//        }
//    }
//
//    private fun initRecyclerView() = binding.rvMusicLibrary.apply {
//        musicAdapter = MusicAdapter()
//        adapter = musicAdapter
//        layoutManager = LinearLayoutManager(this@MainActivity)
//    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_tab_one, R.id.nav_tab_two
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)
    }
}