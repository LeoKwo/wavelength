package com.example.wavelength

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.example.wavelength.databinding.ActivityCreatePlayListBinding
import com.example.wavelength.databinding.ActivityPlayListBinding
import com.example.wavelength.requestBody.CreatePlayListBody
import com.example.wavelength.retrofit.RetrofitInstance
import retrofit2.HttpException
import java.io.FileNotFoundException
import java.io.IOException

private lateinit var activityCreatePlayListBinding: ActivityCreatePlayListBinding
//private var urlNotValid = false

class CreatePlayListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCreatePlayListBinding = ActivityCreatePlayListBinding.inflate(layoutInflater)
        setContentView(activityCreatePlayListBinding.root)

        // action bar options
        supportActionBar?.apply {
            title = "New Playlist"
            elevation = 0F
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // functionality
        with(activityCreatePlayListBinding) {
            // help users validate their urls
            btValidate.setBackgroundColor(ContextCompat.getColor(this@CreatePlayListActivity, R.color.orange))
            btValidate.setOnClickListener {
                val url = etUrl.text
                Glide
                    .with(this@CreatePlayListActivity)
                    .load(url.toString())
                    .centerCrop()
                    .error(R.drawable.ic_error)
                    .transition(withCrossFade())
                    .into(ivPreview)
            }

            // save playlist to database
            btSave.setOnClickListener {
                if (etName.text.toString() == "" || etUrl.text.toString() == "") {
                    Toast.makeText(
                        this@CreatePlayListActivity,
                        "Both text fields are required!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val name = etName.text.toString()
                    val url = etUrl.text.toString()

                    lifecycleScope.launchWhenCreated {
                        val res = try {
                            RetrofitInstance.api.createPlayList(CreatePlayListBody(name, url))
                        } catch (e: IOException) {
                            Toast.makeText(
                                this@CreatePlayListActivity,
                                "io error: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launchWhenCreated
                        } catch (e: HttpException) {
                            Toast.makeText(
                                this@CreatePlayListActivity,
                                "http error",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launchWhenCreated
                        }
                        if (res.isSuccessful) { // 200 status code
                            Toast.makeText(this@CreatePlayListActivity, "New playlist created", Toast.LENGTH_SHORT).show()
                            this@CreatePlayListActivity.finish()
                        } else {
                            Toast.makeText(this@CreatePlayListActivity, "Error creating new playlist", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    // add back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}