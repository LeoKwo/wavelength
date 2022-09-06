package com.example.wavelength.api

import com.example.wavelength.model.Song
import retrofit2.Response
import retrofit2.http.GET

interface MusicAPI {
    // for testing
    @GET("/todos")
    suspend fun getSongs(): Response<List<Song>>

}