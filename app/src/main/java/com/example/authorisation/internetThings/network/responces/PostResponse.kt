package com.example.authorisation.internetThings.network.responces

import com.google.gson.annotations.SerializedName

data class PostResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("element")
    val item: TODOItem,


    @SerializedName("revision")
    val revision: Int

)