package com.eundmswlji.tacoling.domain.repository

import com.eundmswlji.tacoling.data.model.Alarm
import com.eundmswlji.tacoling.data.model.FavoriteShop
import com.eundmswlji.tacoling.data.model.PatchFavoriteShopRes
import com.eundmswlji.tacoling.data.model.UserInfo
import com.eundmswlji.tacoling.data.model.UserKey
import com.eundmswlji.tacoling.domain.model.AlarmModel
import com.eundmswlji.tacoling.domain.model.UserInfoModel
import com.eundmswlji.tacoling.domain.model.UserKeyModel
import com.eundmswlji.tacoling.domain.status.UiState
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface UserRepository {

    suspend fun signUp(
        userInfo: UserInfo
    ): Flow<UiState<UserKeyModel?>>

    suspend fun deleteUser(
        userKey: String
    ): Flow<UiState<ResponseBody?>>

    suspend fun getUserInfo(
        userKey: String
    ): Flow<UiState<UserInfoModel?>>

//
//    suspend fun getFavoriteShops(
//        userKey: String
//    ): Flow<UiState<List<FavoriteShop>?>>

//    suspend fun isFavoriteShop(
//        userKey: String,
//        shopId: Int
//    ): Boolean
//
//    suspend fun getFavoriteShopIndex(
//        userKey: String,
//        shopId: Int
//    ): Int
//
//    suspend fun getFavoriteShopsSize(
//        userKey: String,
//    ): Int

    suspend fun addFavoriteShop(
        userKey: String,
        favoriteShopsSize: Int, //내가 찜한 목록의 size
        favoriteShop: FavoriteShop
    ): Flow<UiState<PatchFavoriteShopRes?>>

    suspend fun deleteFavoriteShop(
        userKey: String,
        indexInFavorites: Int //삭제 하고자 하는 가게의 내가 찜한 목록의 index
    ): Flow<UiState<ResponseBody?>>

    suspend fun patchAlarm(
        userKey: String,
        alarm: Alarm
    ): Flow<UiState<AlarmModel?>>

    suspend fun saveUserKey(userKey: String)
    suspend fun getUserKey(): String?
    suspend fun clearUserKey()

}