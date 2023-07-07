package com.example.authorisation.internetThings

import com.example.authorisation.internetThings.network.responces.GetListAPI
import com.example.authorisation.internetThings.network.responces.PatchListAPI
import com.example.authorisation.internetThings.network.responces.PostResponse
import com.example.authorisation.internetThings.network.responces.PostRequest
import retrofit2.Response
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
    suspend fun getList(): Response<GetListAPI>

    @PATCH("list")
    suspend fun updateList(
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Body list: PatchListAPI
    ): Response<GetListAPI>

    @POST("list")
    suspend fun postElement(
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Body element: PostRequest
    ): Response<PostResponse>

    @DELETE("list/{id}")
    suspend fun deleteElement(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
    ): Response<PostResponse>

    @PUT("list/{id}")
    suspend fun updateElement(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") lastKnownRevision: Int,
        @Body item: PostRequest
    ): Response<PostResponse>

}