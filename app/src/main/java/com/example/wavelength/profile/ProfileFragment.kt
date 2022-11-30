package com.example.wavelength.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wavelength.LoginActivity
import com.example.wavelength.R
import com.example.wavelength.SettingsActivity
import com.example.wavelength.databinding.FragmentPlaylistBinding
import com.example.wavelength.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(layoutInflater)

        binding.btSettings.setOnClickListener {
            startActivity(Intent(context, SettingsActivity::class.java))
        }

//        binding.btLogin.setOnClickListener {
//            startActivity(Intent(context, LoginActivity::class.java))
//        }

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false)
        return binding.root
    }
}