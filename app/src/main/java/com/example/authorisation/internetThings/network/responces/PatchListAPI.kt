package com.example.authorisation.internetThings.network.responces

import com.google.gson.annotations.SerializedName

data class PatchListAPI(
    @SerializedName("list")
    val list: List<TODOItem>
)