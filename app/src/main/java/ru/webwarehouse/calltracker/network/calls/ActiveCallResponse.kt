package ru.webwarehouse.calltracker.network.calls

import com.squareup.moshi.Json

data class ActiveCallResponse(

    @Json(name = "id")
    val id: String,

)
