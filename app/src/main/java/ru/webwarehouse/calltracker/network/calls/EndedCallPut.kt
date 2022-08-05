package ru.webwarehouse.calltracker.network.calls

import com.squareup.moshi.Json

data class EndedCallPut(

    @Json(name = "duration")
    val duration: Int,

)
