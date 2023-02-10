package com.eundmswlji.tacoling.data.repository.user

import com.eundmswlji.tacoling.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Path

interface UserRepository {

    fun postUser(
        @Body body: UserInfo
    ): Response<UserResponse>

    fun deleteUser(
        @Path("id") userId: Int
    ): Response<UserResponse>

    fun getUserInfo(
        @Path("id") userId: Int
    ): Response<UserInfo>

    fun getUserLikedShops(
        @Path("id") userId: Int
    ): Response<List<LikedShop>>

    fun addLikedShop(
        @Path("id") userId: Int,
        @Path("shopId") shopId: Int,
    ): Response<AddLikedShopResponse>

    fun deleteLikedShop(
        @Path("id") userId: Int,
        @Path("shopId") shopId: Int,
    ): Response<Nothing>

    fun patchAlarm(
        @Path("id") userId: Int,
        @Body body: Alarm
    ): Response<Alarm>

}