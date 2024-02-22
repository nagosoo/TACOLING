package com.eundmswlji.tacoling.data.repository.user

import com.eundmswlji.tacoling.data.model.Alarm
import com.eundmswlji.tacoling.data.model.FavoriteShop
import com.eundmswlji.tacoling.data.model.PatchFavoriteShopRes
import com.eundmswlji.tacoling.data.model.UserInfo
import com.eundmswlji.tacoling.data.safeApiCall
import com.eundmswlji.tacoling.data.source.local.user.UserLocalDataSource
import com.eundmswlji.tacoling.data.source.remote.user.UserRemoteDataSource
import com.eundmswlji.tacoling.data.toDomainModelFlow
import com.eundmswlji.tacoling.domain.model.AlarmModel
import com.eundmswlji.tacoling.domain.model.UserInfoModel
import com.eundmswlji.tacoling.domain.model.UserKeyModel
import com.eundmswlji.tacoling.domain.repository.UserRepository
import com.eundmswlji.tacoling.domain.status.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okhttp3.ResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : UserRepository {

    override suspend fun signUp(userInfo: UserInfo): Flow<UiState<UserKeyModel?>> =
        safeApiCall { userRemoteDataSource.signUp(userInfo) }.map { uiState ->
            uiState.toDomainModelFlow()
        }

    override suspend fun deleteUser(userKey: String): Flow<UiState<ResponseBody?>> =
        safeApiCall { userRemoteDataSource.deleteUser(userKey) }

    override suspend fun getUserInfo(userKey: String): Flow<UiState<UserInfoModel?>> =
        safeApiCall { userRemoteDataSource.getUserInfo(userKey) }.map { uiState ->
            uiState.toDomainModelFlow()
        }

//    override suspend fun getFavoriteShops(userKey: String): Flow<UiState<List<FavoriteShop>?>> =
//        safeApiCall { userRemoteDataSource.getFavoriteShops(userKey) }
//        return if (response.isSuccessful) {
//            val list = response.body()!!.filter { it.id > 0 }
//            flow { emit(list) } //임의처리
//        } else flow { emit(emptyList()) }

//    override suspend fun isFavoriteShop(userKey: String, shopId: Int): Boolean {
//        val response = userRemoteDataSource.getFavoriteShops(userKey)
//        return if (response.isSuccessful) {
//            response.body()?.map { shop -> shop.id }?.none { id -> id == shopId }?.not() ?: false
//        } else false
//    }
//
//    override suspend fun getFavoriteShopIndex(userKey: String, shopId: Int): Int {
//        val response = userRemoteDataSource.getFavoriteShops(userKey)
//        return if (response.isSuccessful) {
//            response.body()!!.indexOfFirst { shop ->
//                shop.id == shopId
//            }
//        } else -1
//    }
//
//    override suspend fun getFavoriteShopsSize(userKey: String): Int {
//        val response = userRemoteDataSource.getFavoriteShops(userKey)
//        return if (response.isSuccessful) {
//            response.body()!!.size
//        } else 1
//    }

    override suspend fun addFavoriteShop(
        userKey: String,
        favoriteShopsSize: Int,
        favoriteShop: FavoriteShop
    ): Flow<UiState<PatchFavoriteShopRes?>> = safeApiCall {
        userRemoteDataSource.addFavoriteShop(
            userKey,
            favoriteShopsSize,
            favoriteShop
        )
    }
//    {
//        return if (favoriteShopsSize == null)
//            userRemoteDataSource.addFavoriteShop(userKey, getFavoriteShopsSize(userKey), favoriteShop)
//        else userRemoteDataSource.addFavoriteShop(userKey, favoriteShopsSize, favoriteShop)
//    }

    override suspend fun deleteFavoriteShop(
        userKey: String,
        indexInFavorites: Int
    ): Flow<UiState<ResponseBody?>> =
        safeApiCall { userRemoteDataSource.deleteFavoriteShop(userKey, indexInFavorites) }
//        val shopIndexInLikedShopList = getFavoriteShopIndex(userKey, indexInFavorites)
//        val errorBody =
//            "{ \"message\" : \"Not Found\" }".toResponseBody("application/json".toMediaTypeOrNull())
//        return if (shopIndexInLikedShopList < 0) Response.error(404, errorBody)


    override suspend fun patchAlarm(userKey: String, alarm: Alarm): Flow<UiState<AlarmModel?>> =
        safeApiCall { userRemoteDataSource.patchAlarm(userKey, alarm) }.map { uiState ->
            uiState.toDomainModelFlow()
        }

    override suspend fun saveUserKey(userKey: String) = userLocalDataSource.safeUserKey(userKey)

    override suspend fun getUserKey(): String? {
        return userLocalDataSource.getUserKey().first()
    }

    override suspend fun clearUserKey() {
        userLocalDataSource.clearUserKey()
    }

}