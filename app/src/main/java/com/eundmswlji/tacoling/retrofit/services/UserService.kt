package com.eundmswlji.tacoling.retrofit.services

import com.eundmswlji.tacoling.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {

    @POST("/user.json")
    fun postUser(
        @Body body: UserInfo
    ): Response<UserResponse>

    @DELETE("/user/{id}.json")
    fun deleteUser(
        @Path("id") userId: Int
    ): Response<UserResponse>

    @GET("/user/{id}.json")
    fun getUserInfo(
        @Path("id") userId: Int
    ): Response<UserInfo>

    @GET("/user/{id}/liked_shops.json")
    fun getUserLikedShops(
        @Path("id") userId: Int
    ): Response<List<LikedShop>>

    @PATCH("/user/{id}/liked_shops/{shopId}.json")
    fun addLikedShop(
        @Path("id") userId: Int,
        @Path("shopId") shopId: Int,
    ): Response<AddLikedShopResponse>

    @DELETE("/user/{id}/liked_shops/{shopId}.json")
    fun deleteLikedShop(
        @Path("id") userId: Int,
        @Path("shopId") shopId: Int,
    ): Response<Nothing>

    @PATCH("/user/{id}.json")
    fun patchAlarm(
        @Path("id") userId: Int,
        @Body body: Alarm
    ): Response<Alarm>
}