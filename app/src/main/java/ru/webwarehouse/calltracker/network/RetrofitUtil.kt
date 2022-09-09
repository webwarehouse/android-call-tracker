package ru.webwarehouse.calltracker.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun retrofitInstance(baseUrl: String): Retrofit {
    return Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()
}

/*val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()*/
