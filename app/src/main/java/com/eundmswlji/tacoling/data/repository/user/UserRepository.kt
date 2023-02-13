package com.eundmswlji.tacoling.data.repository.user

import com.eundmswlji.tacoling.data.model.*
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Path

interface UserRepository {

    suspend fun postUser(
        @Body body: UserInfo
    ): Response<UserResponse>

    suspend fun deleteUser(
        @Path("id") userId: String
    ): Response<UserResponse>

//    suspend fun getUserInfo(
//        @Path("id") userId: Int
//    ): Response<UserInfo>

    suspend fun getUserAlarmInfo(
        @Path("id") userId: String
    ): Boolean

    suspend fun getUserLikedShops(
        @Path("id") userId: String
    ): Response<List<LikedShop>>

    suspend fun addLikedShop(
        @Path("id") userId: String,
        @Path("shopId") shopId: Int,
    ): Response<AddLikedShopResponse>

    suspend fun deleteLikedShop(
        @Path("id") userId: String,
        @Path("shopId") shopId: Int,
    ): Response<Nothing>

    suspend fun patchAlarm(
        @Path("id") userId: String,
        @Body body: Alarm
    ): Response<Alarm>

    suspend fun saveUserId(userId: String)
    suspend fun getUserId() : Flow<String?>
}