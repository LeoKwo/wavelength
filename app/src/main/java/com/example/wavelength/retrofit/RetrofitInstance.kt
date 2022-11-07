package com.example.wavelength.retrofit

import com.example.wavelength.api.MusicAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: MusicAPI by lazy {
        Retrofit.Builder()
//            .baseUrl("https://wavelength-app.herokuapp.com/api/")// heroku deploy (for deployment)
            .baseUrl("http://10.0.2.2:8093/api/") // 10.0.2.2 is localhost (for localhost testing)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicAPI::class.java)
    }
}