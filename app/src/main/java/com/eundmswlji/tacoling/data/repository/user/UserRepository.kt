package com.eundmswlji.tacoling.data.repository.user

import com.eundmswlji.tacoling.data.model.*
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface UserRepository {

    suspend fun postUser(
        body: UserInfo
    ): Response<UserResponse>

    suspend fun deleteUser(
        userId: String
    ): Response<ResponseBody>

//    suspend fun getUserInfo(
//        userId: Int
//    ): Response<UserInfo>

    suspend fun getUserAlarmInfo(
        userId: String
    ): Boolean

//    suspend fun getUserLikedShops(
//        userId: String
//    ): Response<List<LikedShop?>>

    suspend fun getUserLikedShops(
        userId: String
    ): Flow<List<LikedShopX>>

    suspend fun isShopInLikedList(
        userId: String,
        shopId: Int
    ): Boolean

    suspend fun getIndexInLikedShopList(
        userId: String,
        shopId: Int
    ): Int

    suspend fun getLikedShopListSize(
        userId: String,
    ): Int

    suspend fun addLikedShop(
        userId: String,
        shopIndex: Int?=null,
        body: LikedShopX
    ): Response<AddLikedShopResponse>

    suspend fun deleteLikedShop(
        userId: String,
        shopId: Int
    ): Response<ResponseBody>

    suspend fun patchAlarm(
        userId: String,
        body: Alarm
    ): Response<Alarm>

    suspend fun saveUserId(userId: String)
    suspend fun getUserId(): String?
    suspend fun clearUserId()

}