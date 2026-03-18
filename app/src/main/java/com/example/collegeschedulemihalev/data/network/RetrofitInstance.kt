package com.example.collegeschedulemihalev.data.network

import com.example.collegeschedulemihalev.data.api.ScheduleApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:5057/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ScheduleApi by lazy {
        retrofit.create(ScheduleApi::class.java)
    }
}