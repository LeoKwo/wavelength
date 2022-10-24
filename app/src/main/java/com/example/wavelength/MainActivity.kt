package com.example.wavelength

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
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

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_tab_one, R.id.nav_tab_two, R.id.nav_tab_three
            )
        )


        supportActionBar?.title = "Wavelength"

        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)
    }
}