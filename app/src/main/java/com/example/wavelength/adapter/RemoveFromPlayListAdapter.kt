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
import com.example.wavelength.databinding.ItemAddToPlayListBinding
import com.example.wavelength.databinding.ItemPlayListBinding
import com.example.wavelength.databinding.ItemSongBinding
import com.example.wavelength.model.PlayList
import com.example.wavelength.model.Song
import com.example.wavelength.playList.PlayListFragment
import com.example.wavelength.retrofit.RetrofitInstance
import retrofit2.HttpException
import java.io.IOException

class RemoveFromPlayListAdapter: RecyclerView.Adapter<RemoveFromPlayListAdapter.RemoveFromPlayListViewHolder>(){
    var onPlayListClickListener: (playList: PlayList) -> Unit = {_ ->}



//    var onPlayListRemoveClickListener: (playList: PlayList) -> Unit = {_ ->}
//    var onPlayListEditClickListener: (playList: PlayList) -> Unit = {_ ->}

    inner class RemoveFromPlayListViewHolder(val binding: ItemAddToPlayListBinding) : RecyclerView.ViewHolder(binding.root)

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

    var playlists: List<PlayList>
        get() = differ.currentList
        set(value) {differ.submitList(value)}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemoveFromPlayListViewHolder {
        context = parent.context
        return RemoveFromPlayListViewHolder(ItemAddToPlayListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: RemoveFromPlayListViewHolder, position: Int) {
        holder.binding.apply {
            val playList = playlists[position]
            tvPlayListName.text = playList.name

            // set btRemove button style and functionality
//            btRemove.setColorFilter(ContextCompat.getColor(context, R.color.red))

            // set favorites playlist icon properties
//            if (playList.id == "1") { // Favorites
//                // tint favorites heart icon
//                ivPlayListArt.setColorFilter(ContextCompat.getColor(context, R.color.orange))
//                btRemove.visibility = View.GONE
//            }

            // set non-favorites playlist icon properties
            if (playList.artUrl != "") {
                Log.i("playlistarturl", playList.artUrl)
                Glide.with(context)
                    .load(playList.artUrl)
                    .centerCrop()
                    .into(ivPlayListArt)
            }

            ivRemove.setColorFilter(ContextCompat.getColor(context, R.color.orange))
            ivRemove.visibility = View.VISIBLE
            ivRemove.setOnClickListener{onPlayListClickListener(playList)}

//            clPlayList.setOnClickListener{onPlayListClickListener(playList)}

//            clPlayList.setOnLongClickListener {
//                val builder = AlertDialog.Builder(context)
//                builder.setMessage("Edit ${playList.name} playlist")
//                builder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
//                    onPlayListRemoveClickListener(playList)
//                })
//                builder.setNegativeButton("Cancel") { _, _ -> }
//                builder.setNeutralButton("Edit", DialogInterface.OnClickListener { _, _ ->
//                    onPlayListEditClickListener(playList)
//                })
//
//                builder.show()
//                true
//            }
        }
    }

    override fun getItemCount() = playlists.size
}