package ru.webwarehouse.calltracker.network.calls

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.webwarehouse.calltracker.network.retrofitInstance

interface CallsApiService {

    @POST(value = "api/calls")
    fun postActiveCall(

        @Body
        body: ActiveCallPost,

    ): Call<ActiveCallResponse>

    @PUT(value = "api/calls/{id}")
    fun postEndedCall(

        @Path("id")
        id: String,

        @Body
        body: EndedCallPut,

        ): Call<Any>

}

class CallsApi(private val baseUrl: String) {

    val retrofitService: CallsApiService by lazy {
        retrofitInstance(baseUrl).create(CallsApiService::class.java)
    }

}
