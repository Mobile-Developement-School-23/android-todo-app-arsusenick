package com.example.authorisation.internetThings.network.responces

import com.google.gson.annotations.SerializedName

data class GetListAPI(
    @SerializedName("status")
    val status: String,
    @SerializedName("list")
    val list: List<TODOItem>,
    @SerializedName("revision")
    val revision: Int

    )