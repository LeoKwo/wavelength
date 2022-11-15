package com.example.wavelength.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavelength.R
import com.example.wavelength.databinding.ItemPlayListBinding
import com.example.wavelength.databinding.ItemSongBinding
import com.example.wavelength.model.PlayList
import com.example.wavelength.model.Song
import com.example.wavelength.playList.PlayListFragment
import com.example.wavelength.retrofit.RetrofitInstance
import retrofit2.HttpException
import java.io.IOException

class PlayListAdapter: RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder>(){
    var onPlayListClickListener: (playList: PlayList) -> Unit = {_ ->}
    var onPlayListRemoveClickListener: (playList: PlayList) -> Unit = {_ ->}
    var onPlayListEditClickListener: (playList: PlayList) -> Unit = {_ ->}
//    var onPlayListLongClickListener: (playList: PlayList) -> Boolean = {  }

    inner class PlayListViewHolder(val binding: ItemPlayListBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<PlayList>() {
        override fun areItemsTheSame(oldItem: PlayList, newItem: PlayList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlayList, newItem: PlayList): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    private lateinit var context: Context

    var playLists: List<PlayList>
        get() = differ.currentList
        set(value) {differ.submitList(value)}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayListViewHolder {
        context = parent.context
        return PlayListViewHolder(ItemPlayListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: PlayListViewHolder, position: Int) {
        holder.binding.apply {
            val playList = playLists[position]
            tvPlayListName.text = playList.name

            // set btRemove button style and functionality
            btRemove.setColorFilter(ContextCompat.getColor(context, R.color.red))

            // set favorites playlist icon properties
            if (playList.id == "1") { // Favorites
                // tint favorites heart icon
                ivPlayListArt.setColorFilter(ContextCompat.getColor(context, R.color.orange))
                btRemove.visibility = View.GONE
            }

            // set non-favorites playlist icon properties
            if (playList.artUrl != "") {
                Log.i("playlistarturl", playList.artUrl)
                Glide.with(context)
                    .load(playList.artUrl)
                    .centerCrop()
                    .into(ivPlayListArt)
            }

            clPlayList.setOnClickListener{onPlayListClickListener(playList)}

//            btRemove.setOnClickListener{onPlayListRemoveClickListener(playList)}

//            btRemove.setOnLongClickListener{ onPlayListLongClickListener(playList) }
            if (playList.id == "1") {
                clPlayList.setOnLongClickListener {
                    Toast.makeText(context, "Favorites cannot be deleted or renamed", Toast.LENGTH_SHORT).show()
                    true
                }
            } else {
                clPlayList.setOnLongClickListener {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("Edit ${playList.name} playlist")
                    builder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
                        onPlayListRemoveClickListener(playList)
                    })
                    builder.setNegativeButton("Cancel") { _, _ -> }
                    builder.setNeutralButton("Edit", DialogInterface.OnClickListener { _, _ ->
                        onPlayListEditClickListener(playList)
                    })

                    builder.show()
                    true
                }
            }



        }
    }

    override fun getItemCount() = playLists.size
}