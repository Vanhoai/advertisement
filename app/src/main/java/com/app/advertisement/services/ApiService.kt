package com.app.advertisement.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiService() {

    private val url = "http://113.161.84.249:8081/video/api/Adver/list-video/"
    var api: ApiEndpoint;

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ApiEndpoint::class.java)
    }
}