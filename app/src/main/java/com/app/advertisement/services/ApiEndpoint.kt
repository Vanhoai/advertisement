package com.app.advertisement.services

import com.app.advertisement.models.LinkResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiEndpoint {

    @GET
    fun getLinkVideo(@Url url: String): Call<LinkResponse>;

}