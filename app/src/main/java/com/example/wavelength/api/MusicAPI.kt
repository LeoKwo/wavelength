package com.example.wavelength.api

import com.example.wavelength.model.PlayList
import com.example.wavelength.model.Song
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MusicAPI {
    // for testing
    @GET("all")
    suspend fun getSongs(): Response<List<Song>>

    @GET("playLists")
    suspend fun getPlayLists(): Response<List<PlayList>>

    @GET("favs")
    suspend fun getFavSongs(): Response<List<Song>>

    @POST("fav/{id}/{fav}")
    suspend fun favSong(@Path("id") id: String, @Path("fav") fav: Boolean)

    @POST("search/{query}")
    suspend fun searchSong(@Path("query") query: String): Response<List<Song>>

    @GET("playLists/{id}")
    suspend fun getPlayList(@Path("id") id: String): Response<PlayList>

    @POST("playLists/remove/{id}")
    suspend fun removePlayList(@Path("id") id: String): Response<Void>
}