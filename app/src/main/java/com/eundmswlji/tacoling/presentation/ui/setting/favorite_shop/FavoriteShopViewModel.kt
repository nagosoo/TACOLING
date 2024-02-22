package com.eundmswlji.tacoling.presentation.ui.setting.favorite_shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eundmswlji.tacoling.domain.model.FavoriteShopModel
import com.eundmswlji.tacoling.domain.status.UiState
import com.eundmswlji.tacoling.domain.usecase.user.AddFavoriteShopUseCase
import com.eundmswlji.tacoling.domain.usecase.user.DeleteFavoriteShopUseCase
import com.eundmswlji.tacoling.domain.usecase.user.GetUserFavoriteShopsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteShopViewModel @Inject constructor(
    private val getUserFavoriteShopsUseCase: GetUserFavoriteShopsUseCase,
    private val addFavoriteShopUseCase: AddFavoriteShopUseCase,
    private val deleteFavoriteShopUseCase: DeleteFavoriteShopUseCase,
) : ViewModel() {

    private val _favoriteShops =
        MutableStateFlow<UiState<List<FavoriteShopModel>?>>(UiState.Loading)
    val favoriteShops: StateFlow<UiState<List<FavoriteShopModel>?>> = _favoriteShops

    fun getLikedShops(userKey: String) {
        viewModelScope.launch {
            getUserFavoriteShopsUseCase(userKey)
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    UiState.Loading
                )
                .collect { shops ->
                    _favoriteShops.value = shops
                }
        }
    }

    fun removeFavoriteShop(userKey: String, indexOfFavoriteShops: Int) {
        _favoriteShops.value = UiState.Loading
        viewModelScope.launch {
            deleteFavoriteShopUseCase(userKey, indexOfFavoriteShops)
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    UiState.Loading
                )
                .collect { uiState ->
                    if (uiState is UiState.Success) {
                        _favoriteShops.value = UiState.Success(
                            (_favoriteShops.value as UiState.Success).value?.toMutableList()
                                ?.apply {
                                    removeAt(indexOfFavoriteShops)
                                }
                        )
                    }
                }
        }
    }

    //되돌리기 눌렀을 때
    fun addFavoriteShop(shopIndex: Int, shopId: Int, shopName: String) {
//        userId?.let { userId ->
//            viewModelScope.launch {
//                val shop = FavoriteShop(shopId, shopName)
//                val response =
//                    userRepository.addFavoriteShop(userId, shopIndex + 1, shop)
//                if (response.isSuccessful) {
//                    // 그 포자션만 notify
//                    cachedFavoriteShops.add(shopIndex, shop)
//                    listUpdated.postValue(shopIndex)
//                } else {
//                    _toastHelper.postValue(Event(response.errorBody()?.string() ?: "error"))
//                }
//            }
//        }
    }

}