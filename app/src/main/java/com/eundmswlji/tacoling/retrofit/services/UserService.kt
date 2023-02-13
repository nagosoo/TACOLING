package com.eundmswlji.tacoling.retrofit.services

import com.eundmswlji.tacoling.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface UserService {

    @POST("/user.json")
    suspend fun postUser(
        @Body body: UserInfo
    ): Response<UserResponse>

    @DELETE("/user/{id}.json")
    suspend fun deleteUser(
        @Path("id") userId: String
    ): Response<UserResponse>

    @GET("/user/{id}.json")
    suspend fun getUserInfo(
        @Path("id") userId: String
    ): Response<UserInfo>

    @GET("/user/{id}/liked_shops.json")
    suspend fun getUserLikedShops(
        @Path("id") userId: String
    ): Response<List<LikedShop>>

    @PATCH("/user/{id}/liked_shops/{shopId}.json")
    suspend fun addLikedShop(
        @Path("id") userId: String,
        @Path("shopId") shopId: Int,
    ): Response<AddLikedShopResponse>

    @DELETE("/user/{id}/liked_shops/{shopId}.json")
    suspend fun deleteLikedShop(
        @Path("id") userId: String,
        @Path("shopId") shopId: Int,
    ): Response<Nothing>

    @PATCH("/user/{id}.json")
    suspend fun patchAlarm(
        @Path("id") userId: String,
        @Body body: Alarm
    ): Response<Alarm>
}