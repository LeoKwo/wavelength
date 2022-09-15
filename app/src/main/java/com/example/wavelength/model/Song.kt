package com.example.wavelength.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
//    val completed: Boolean,
//    val id: Int,
//    val title: String,
//    val userId: Int
    val id: String,
    val songName: String,
    val albumName: String,
    val artistName: String,
    val isFavorite: Boolean
) : Parcelable