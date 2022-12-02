package com.example.wavelength.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wavelength.LoginActivity
import com.example.wavelength.PurchaseActivity
import com.example.wavelength.R
import com.example.wavelength.SettingsActivity
import com.example.wavelength.databinding.FragmentPlaylistBinding
import com.example.wavelength.databinding.FragmentProfileBinding
import okhttp3.internal.immutableListOf

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        binding.llSettings.setOnClickListener {
            startActivity(Intent(context, SettingsActivity::class.java))
        }

        binding.btnGetPremium.setOnClickListener {
            startActivity(Intent(context, PurchaseActivity::class.java))
        }
        //        return inflater.inflate(R.layout.fragment_profile, container, false)
        return binding.root
    }


}