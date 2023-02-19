package com.eundmswlji.tacoling.data.repository.user

import com.eundmswlji.tacoling.data.model.*
import com.eundmswlji.tacoling.data.source.local.user.UserLocalDataSource
import com.eundmswlji.tacoling.data.source.remote.user.UserRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : UserRepository {

    private var USER_ID: String? = null

    override suspend fun postUser(body: UserInfo): Response<UserResponse> =
        userRemoteDataSource.postUser(body)

    override suspend fun deleteUser(userId: String): Response<ResponseBody> =
        userRemoteDataSource.deleteUser(userId)

    //    override suspend fun getUserInfo(userId: Int): Response<UserInfo> =
//        userRemoteDataSource.getUserInfo(userId)
    override suspend fun getUserAlarmInfo(userId: String): Boolean {
        val response = userRemoteDataSource.getUserInfo(userId)
        return if (response.isSuccessful) response.body()?.notification ?: true
        else true
    }

    override suspend fun getUserLikedShops(userId: String): Flow<List<LikedShopX>> {
        val response = userRemoteDataSource.getUserLikedShops(userId)
        return if (response.isSuccessful) {
            val list = response.body()!!.filter { it.id > 0 }
            flow { emit(list) } //임의처리
        } else flow { emit(emptyList()) }
    }

    override suspend fun isShopInLikedList(userId: String, shopId: Int): Boolean {
        val response = userRemoteDataSource.getUserLikedShops(userId)
        return if (response.isSuccessful) {
            response.body()?.map { shop -> shop.id }?.none { id -> id == shopId }?.not() ?: false
        } else false
    }

    override suspend fun getIndexInLikedShopList(userId: String, shopId: Int): Int {
        val response = userRemoteDataSource.getUserLikedShops(userId)
        return if (response.isSuccessful) {
            response.body()!!.indexOfFirst { shop ->
                shop.id == shopId
            }
        } else -1
    }

    override suspend fun getLikedShopListSize(userId: String): Int {
        val response = userRemoteDataSource.getUserLikedShops(userId)
        return if (response.isSuccessful) {
            response.body()!!.size
        } else 1
    }

    override suspend fun addLikedShop(
        userId: String,
        shopIndex: Int?,
        body: LikedShopX
    ): Response<AddLikedShopResponse> {
        return if (shopIndex == null)
            userRemoteDataSource.addLikedShop(userId, getLikedShopListSize(userId), body)
        else userRemoteDataSource.addLikedShop(userId, shopIndex, body)
    }


//    override suspend fun deleteLikedShop(userId: String, shopIndex: Int): Response<ResponseBody> {
//
//        userRemoteDataSource.deleteLikedShop(userId, shopIndex)
//    }
//

    override suspend fun deleteLikedShop(userId: String, shopId: Int): Response<ResponseBody> {
        val shopIndexInLikedShopList = getIndexInLikedShopList(userId, shopId)
        val errorBody =
            "{ \"message\" : \"Not Found\" }".toResponseBody("application/json".toMediaTypeOrNull())
        return if (shopIndexInLikedShopList < 0) Response.error(404, errorBody)
        else userRemoteDataSource.deleteLikedShop(userId, shopIndexInLikedShopList)
    }


    override suspend fun patchAlarm(userId: String, body: Alarm): Response<Alarm> =
        userRemoteDataSource.patchAlarm(userId, body)

    override suspend fun saveUserId(userId: String) = userLocalDataSource.saveUserId(userId)

    override suspend fun getUserId(): String? {
        if (USER_ID.isNullOrEmpty()) {
            USER_ID = userLocalDataSource.getUserId().first()
        }
        return USER_ID
    }


    override suspend fun clearUserId() {
        userLocalDataSource.clearUserId()
        USER_ID = null
    }

}