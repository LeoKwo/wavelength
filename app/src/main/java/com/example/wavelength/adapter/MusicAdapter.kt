package com.example.wavelength.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wavelength.model.Song
import com.example.wavelength.databinding.ItemSongBinding

class MusicAdapter: RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {
    inner class MusicViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var todos : List<Song>
        get() = differ.currentList
        set(value) {differ.submitList(value)}

    override fun getItemCount() = todos.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        return MusicViewHolder(ItemSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.binding.apply {
            val todo = todos[position]
            tvSongArtist.text = todo.title
            tvSongTitle.text = todo.id.toString()
            
        }
    }
}