package com.eundmswlji.tacoling.data.source.remote.user

import com.eundmswlji.tacoling.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UserRemoteDataSource {

    suspend fun postUser(
        @Body body: UserInfo
    ): Response<UserResponse>

    suspend fun deleteUser(
        @Path("id") userId: String
    ): Response<ResponseBody>

    suspend fun getUserInfo(
        @Path("id") userId: String
    ): Response<UserInfo>

    suspend fun getUserLikedShops(
        @Path("id") userId: String
    ): Response<List<LikedShopX>>

    suspend fun addLikedShop(
        @Path("id") userId: String,
        @Path("shopId") shopIndex: Int,
        @Body body: LikedShopX
    ): Response<AddLikedShopResponse>

    suspend fun deleteLikedShop(
        @Path("id") userId: String,
        @Path("shopId") shopIndex: Int,
    ): Response<ResponseBody>

    suspend fun patchAlarm(
        @Path("id") userId: String,
        @Body body: Alarm
    ): Response<Alarm>

}