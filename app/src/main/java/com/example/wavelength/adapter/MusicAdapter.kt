package com.example.wavelength.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.wavelength.model.Song
import com.example.wavelength.databinding.ItemSongBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
//    var todos : List<Song>
    var songs: List<Song>
        get() = differ.currentList
        set(value) {differ.submitList(value)}

    override fun getItemCount() = songs.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        return MusicViewHolder(ItemSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.binding.apply {
            val song = songs[position]

//            val albumArtDocument = Firebase.firestore.collection("songs").document("")
//            ivAlbumArt.setImageResource()

            tvSongArtist.text = song.artistName
            tvSongTitle.text = song.songName
            tvSongAlbum.text = song.albumName
            ivAlbumArt.load("https://musiclibrary.nyc3.cdn.digitaloceanspaces.com/${song.songName}.jpeg") {
                crossfade(true)
            }

            clSongItem.setOnClickListener{
                onSongClickListener(song)
            }
//            ivFavButton.visibility = View.VISIBLE
            if (song.isFavorite) ivFavButton.visibility = View.VISIBLE
//            ivFavButton.visibility = if song.isFavorite View.VISIBLE else View.INVISIBLE
            Log.i("name", song.toString())
//            tvSongArtist.text = todo.title
//            tvSongTitle.text = todo.id.toString()
        }
    }
//    inner class MusicViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root)
//
//    private val diffCallback = object : DiffUtil.ItemCallback<Song>() {
//        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//    private val differ = AsyncListDiffer(this, diffCallback)
//    var todos : List<Song>
//        get() = differ.currentList
//        set(value) {differ.submitList(value)}
//
//    override fun getItemCount() = todos.size
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
//        return MusicViewHolder(ItemSongBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        ))
//    }
//
//    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
//        holder.binding.apply {
//            val todo = todos[position]
//
//            tvSongArtist.text = todo.title
//            tvSongTitle.text = todo.id.toString()
//
//        }
//    }
}