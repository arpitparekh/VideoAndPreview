package com.example.videoandpreview

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface API {

    @POST("v4/unAuthRoutes")
    fun sendData(@Query("video")videoString : String) : Call<Void>

}