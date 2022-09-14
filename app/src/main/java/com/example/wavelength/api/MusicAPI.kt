package com.example.wavelength.api

import com.example.wavelength.model.Song
import retrofit2.Response
import retrofit2.http.GET

interface MusicAPI {
    // for testing
    @GET("all")
    suspend fun getSongs(): Response<List<Song>>

}