package ru.webwarehouse.calltracker.network.calls

import retrofit2.Call
import retrofit2.http.*
import ru.webwarehouse.calltracker.network.retrofitInstance
import ru.webwarehouse.calltracker.util.UserAgent

interface CallsApiService {

    @POST(value = "api/calls")
    fun postActiveCall(

        @Body
        body: ActiveCallPost,

        @Header("Authorization")
        token: String,

        @Header("User-Agent")
        userAgent: String = UserAgent.instance.userAgent,

    ): Call<ActiveCallResponse>

    @PUT(value = "api/calls/{id}")
    fun postEndedCall(

        @Path("id")
        id: String,

        @Body
        body: EndedCallPut,

        @Header("Authorization")
        token: String,

        @Header("User-Agent")
        userAgent: String = UserAgent.instance.userAgent,

    ): Call<Any>

}

class CallsApi(private val baseUrl: String) {

    val retrofitService: CallsApiService by lazy {
        retrofitInstance(baseUrl).create(CallsApiService::class.java)
    }

}
