package ru.webwarehouse.calltracker.network.calls

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import ru.webwarehouse.calltracker.network.retrofitInstance

interface CallsApiService {

    @POST(value = ".")
    fun patchActiveCall(

        @Body
        body: ActiveCallPatch,

    ): Call<ActiveCallResponse>

}

class CallsApi(private val baseUrl: String) {

    val retrofitService: CallsApiService by lazy {
        retrofitInstance(baseUrl).create(CallsApiService::class.java)
    }

}
