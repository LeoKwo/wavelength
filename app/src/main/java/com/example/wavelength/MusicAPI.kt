package com.example.wavelength

import retrofit2.Response
import retrofit2.http.GET

interface MusicAPI {
    // for testing
    @GET("/todos")
    suspend fun getSongs(): Response<List<Todo>>

}