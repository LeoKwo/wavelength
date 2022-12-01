package com.example.wavelength.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val id: String,
    val songName: String,
    val albumName: String,
    val artistName: String,
    var isFavorite: Boolean
) : Parcelable