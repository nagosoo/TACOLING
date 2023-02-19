package com.eundmswlji.tacoling.data.source.remote.user

import com.eundmswlji.tacoling.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response

interface UserRemoteDataSource {

    suspend fun postUser(
        body: UserInfo
    ): Response<UserResponse>

    suspend fun deleteUser(
        userId: String
    ): Response<ResponseBody>

    suspend fun getUserInfo(
        userId: String
    ): Response<UserInfo>

    suspend fun getUserLikedShops(
        userId: String
    ): Response<List<LikedShopX>>

    suspend fun addLikedShop(
        userId: String,
        shopIndex: Int,
        body: LikedShopX
    ): Response<AddLikedShopResponse>

    suspend fun deleteLikedShop(
        userId: String,
        shopIndex: Int,
    ): Response<ResponseBody>

    suspend fun patchAlarm(
        userId: String,
        body: Alarm
    ): Response<Alarm>

}