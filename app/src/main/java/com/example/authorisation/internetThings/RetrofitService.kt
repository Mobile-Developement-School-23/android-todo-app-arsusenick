package com.example.authorisation.internetThings

import com.example.authorisation.internetThings.network.responces.GetListAPI
import com.example.authorisation.internetThings.network.responces.PatchListAPI
import com.example.authorisation.internetThings.network.responces.PostResponse
import com.example.authorisation.internetThings.network.responces.PostRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitService {


    @GET("list")
    suspend fun getList(
        @Header("Authorization") token: String,
    ): GetListAPI

    @PATCH("list")
    suspend fun updateList(
        @Header("Authorization") token: String,
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Body list: PatchListAPI
    ): GetListAPI

    @POST("list")
    suspend fun postElement(
        @Header("Authorization") token: String,
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Body element: PostRequest
    ): PostResponse

    @DELETE("list/{id}")
    suspend fun deleteElement(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
    ): PostResponse

    @PUT("list/{id}")
    suspend fun updateElement(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Body item: PostRequest
    ): PostResponse


}