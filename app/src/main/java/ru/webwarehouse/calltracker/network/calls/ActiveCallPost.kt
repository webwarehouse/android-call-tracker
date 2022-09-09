package ru.webwarehouse.calltracker.network.calls

import com.google.gson.annotations.SerializedName

data class ActiveCallPost(

    //@Json(name = "phone")
    @SerializedName("phone")
    val phone: String,

    //@Json(name = "type")
    @SerializedName("type")
    val type: String,

    //@Json(name = "operator")
    @SerializedName("operator")
    val operator: String,

)
