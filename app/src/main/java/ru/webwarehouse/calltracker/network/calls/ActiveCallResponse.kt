package ru.webwarehouse.calltracker.network.calls

import com.google.gson.annotations.SerializedName

data class ActiveCallResponse(

    //@Json(name = "id")
    @SerializedName("id")
    val id: String,

)
