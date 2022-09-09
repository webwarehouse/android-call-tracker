package ru.webwarehouse.calltracker.network.calls

import com.google.gson.annotations.SerializedName

data class EndedCallPut(

    //@Json(name = "duration")
    @SerializedName("duration")
    val duration: Int,

)
