package com.eundmswlji.tacoling.data.retrofit.services

import com.eundmswlji.tacoling.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UserService {

    @POST("/user.json")
    suspend fun signUp(
        @Body body: UserInfo
    ): Response<UserKey?>

    @DELETE("/user/{id}.json")
    suspend fun deleteUser(
        @Path("id") userId: String
    ): Response<ResponseBody?>

    @GET("/user/{id}.json")
    suspend fun getUserInfo(
        @Path("id") userId: String
    ): Response<UserInfo?>

    @GET("/user/{id}/liked_shops.json")
    suspend fun getFavoriteShops(
        @Path("id") userId: String
    ): Response<List<FavoriteShop>?>

    @PATCH("/user/{id}/liked_shops/{shopId}.json")
    suspend fun addFavoriteShop(
        @Path("id") userId: String,
        @Path("shopId") favoriteShopsSize: Int,
        @Body body: FavoriteShop
    ): Response<PatchFavoriteShopRes?>

    @DELETE("/user/{id}/liked_shops/{shopId}.json")
    suspend fun deleteFavoriteShop(
        @Path("id") userId: String,
        @Path("shopId") indexInFavorites: Int,
    ): Response<ResponseBody?>

    @PATCH("/user/{id}.json")
    suspend fun patchAlarm(
        @Path("id") userId: String,
        @Body body: Alarm
    ): Response<Alarm?>
}