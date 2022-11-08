package com.example.wavelength.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.wavelength.R
import com.example.wavelength.model.Song
import com.example.wavelength.databinding.ItemSongBinding

class MusicAdapter: RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    var onSongClickListener: (song: Song) -> Unit = {_ ->}

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
    private lateinit var context: Context

    var songs: List<Song>
        get() = differ.currentList
        set(value) {differ.submitList(value)}

    override fun getItemCount() = songs.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        context = parent.context
        return MusicViewHolder(ItemSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.binding.apply {
            val song = songs[position]

            tvSongArtist.text = song.artistName
            tvSongTitle.text = song.songName

            // Sometimes coil is buggy running in the emulator
//            ivAlbumArt.load("https://musiclibrary.nyc3.cdn.digitaloceanspaces.com/${song.songName}.jpeg") {
//                crossfade(true)
//            }

            Glide.with(context)
                .load("https://musiclibrary.nyc3.cdn.digitaloceanspaces.com/${song.songName}.jpeg")
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivAlbumArt)

            clSongItem.setOnClickListener{
                onSongClickListener(song)
            }

            if (song.isFavorite) {
                ivFavButton.visibility = View.VISIBLE
            }
        }
    }
}