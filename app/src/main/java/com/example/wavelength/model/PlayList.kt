package com.example.wavelength.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayList(
    val id: String,
    val playListName: String,
    val playListArt: String
) : Parcelable