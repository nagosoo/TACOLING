package com.eundmswlji.tacoling.data.source.remote.user

import com.eundmswlji.tacoling.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface UserDataSource {

    suspend fun postUser(
        @Body body: UserInfo
    ): Response<UserResponse>

    suspend fun deleteUser(
        @Path("id") userId: Int
    ): Response<UserResponse>

    suspend fun getUserInfo(
        @Path("id") userId: Int
    ): Response<UserInfo>

    suspend fun getUserLikedShops(
        @Path("id") userId: Int
    ): Response<List<LikedShop>>

    suspend fun addLikedShop(
        @Path("id") userId: Int,
        @Path("shopId") shopId: Int,
    ): Response<AddLikedShopResponse>

    suspend fun deleteLikedShop(
        @Path("id") userId: Int,
        @Path("shopId") shopId: Int,
    ): Response<Nothing>

    suspend fun patchAlarm(
        @Path("id") userId: Int,
        @Body body: Alarm
    ): Response<Alarm>

}