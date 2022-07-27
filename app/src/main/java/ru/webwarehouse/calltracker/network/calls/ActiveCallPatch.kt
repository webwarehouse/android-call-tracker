package ru.webwarehouse.calltracker.network.calls

import com.squareup.moshi.Json

data class ActiveCallPatch(

    @Json(name = "phone")
    val phone: String,

    @Json(name = "type")
    val type: String,

    @Json(name = "operator")
    val operator: String,

)
