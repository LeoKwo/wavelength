package com.example.wavelength.api

import com.example.wavelength.model.Song
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MusicAPI {
    // for testing
    @GET("all")
    suspend fun getSongs(): Response<List<Song>>

    @POST("fav/{id}/{fav}")
    suspend fun favSong(@Path("id") id: String, @Path("fav") fav: Boolean)
}