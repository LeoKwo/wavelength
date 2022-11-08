package com.example.wavelength

import android.content.Context
import android.content.Intent
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
import com.example.wavelength.databinding.ActivityEditPlayListBinding
import com.example.wavelength.databinding.ActivityPlayListBinding
import com.example.wavelength.model.PlayList
import com.example.wavelength.requestBody.CreatePlayListBody
import com.example.wavelength.requestBody.UpdatePlayListBody
import com.example.wavelength.retrofit.RetrofitInstance
import retrofit2.HttpException
import java.io.FileNotFoundException
import java.io.IOException

private lateinit var activityEditPlayListActivity: ActivityEditPlayListBinding
private lateinit var playList: PlayList
private const val PLAYLIST_KEY = "playlist"
//private var urlNotValid = false
fun navigateToEditPlayListActivity(context: Context, playList: PlayList)  {
    val intent = Intent(context, EditPlayListActivity::class.java)
    val bundle = Bundle()
    bundle.putParcelable(PLAYLIST_KEY, playList)
    intent.putExtras(bundle)
    context.startActivity(intent)
}

class EditPlayListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityEditPlayListActivity = ActivityEditPlayListBinding.inflate(layoutInflater)
        setContentView(activityEditPlayListActivity.root)

        // get playlist
        val launchIntent = intent
        launchIntent.extras?.getParcelable<PlayList>(PLAYLIST_KEY).let { it ->
            if (it != null) {
                playList = it
            }
        }


        // action bar options
        supportActionBar?.apply {
            title = "Edit Your Playlist"
            elevation = 0F
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // functionality
        with(activityEditPlayListActivity) {
            // help users validate their urls
            btValidate.setBackgroundColor(ContextCompat.getColor(this@EditPlayListActivity, R.color.orange))
            btValidate.setOnClickListener {
                val url = etUrl.text
                Glide
                    .with(this@EditPlayListActivity)
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
                        this@EditPlayListActivity,
                        "Both text fields are required!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val name = etName.text.toString()
                    val url = etUrl.text.toString()
                    val songs: List<String> = if (!::playList.isInitialized) {
                        listOf()
                    } else {
                        playList.songs
                    }

                    lifecycleScope.launchWhenCreated {
                        val res = try {
                            RetrofitInstance.api.updatePlayList(playList.id, UpdatePlayListBody(name, url, songs))
                        } catch (e: IOException) {
                            Toast.makeText(
                                this@EditPlayListActivity,
                                "io error: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launchWhenCreated
                        } catch (e: HttpException) {
                            Toast.makeText(
                                this@EditPlayListActivity,
                                "http error",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launchWhenCreated
                        }
                        if (res.isSuccessful) { // 200 status code
                            Toast.makeText(this@EditPlayListActivity, "Playlist ${playList.name} updated", Toast.LENGTH_SHORT).show()
                            this@EditPlayListActivity.finish()
                        } else {
                            Toast.makeText(this@EditPlayListActivity, "Error updating new playlist", Toast.LENGTH_SHORT).show()
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