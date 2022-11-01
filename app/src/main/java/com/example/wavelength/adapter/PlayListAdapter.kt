package com.example.wavelength.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.wavelength.R
import com.example.wavelength.databinding.ItemPlayListBinding
import com.example.wavelength.databinding.ItemSongBinding
import com.example.wavelength.model.PlayList
import com.example.wavelength.model.Song

class PlayListAdapter: RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder>(){
    var onPlayListClickListener: (playList: PlayList) -> Unit = {_ ->}

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
            if (playList.artUrl != "") {
                ivPlayListArt.load(playList.artUrl) {
                    crossfade(true)
                }
            }

            clPlayList.setOnClickListener{onPlayListClickListener(playList)}
        }
    }

    override fun getItemCount() = playLists.size
}