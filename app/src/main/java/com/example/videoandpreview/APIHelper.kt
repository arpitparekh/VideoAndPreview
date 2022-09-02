package com.example.videoandpreview

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIHelper {

    companion object{

        fun getInstance() : API{

            val retroit = Retrofit.Builder()
                .baseUrl("http://111.118.214.237:4050/v4/unAuthRoutes/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retroit.create(API::class.java)

        }

    }

}