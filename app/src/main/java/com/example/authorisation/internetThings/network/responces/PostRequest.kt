package com.example.authorisation.internetThings.network.responces

import com.google.gson.annotations.SerializedName

data class PostRequest(
    @SerializedName("element")
    val item: TODOItem,
)