package com.eundmswlji.tacoling.presentation.ui.shop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eundmswlji.tacoling.data.model.FavoriteShop
import com.eundmswlji.tacoling.data.model.Location
import com.eundmswlji.tacoling.domain.model.FavoriteShopModel
import com.eundmswlji.tacoling.domain.model.ShopModel
import com.eundmswlji.tacoling.domain.status.UiState
import com.eundmswlji.tacoling.domain.usecase.shop.GetShopInfoUseCase
import com.eundmswlji.tacoling.domain.usecase.user.AddFavoriteShopUseCase
import com.eundmswlji.tacoling.domain.usecase.user.DeleteFavoriteShopUseCase
import com.eundmswlji.tacoling.domain.usecase.user.GetUserFavoriteShopsUseCase
import com.eundmswlji.tacoling.presentation.util.MapUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject


@HiltViewModel
class ShopViewModel @Inject constructor(
    private val getShopInfoUseCase: GetShopInfoUseCase,
    private val getUserFavoriteShopsUseCase: GetUserFavoriteShopsUseCase,
    private val addFavoriteShopUseCase: AddFavoriteShopUseCase,
    private val deleteFavoriteShopUseCase: DeleteFavoriteShopUseCase,
    app: Application
) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow<UiState<ShopModel?>>(UiState.Loading)
    val uiState: StateFlow<UiState<ShopModel?>> = _uiState
    val today by lazy { Calendar.getInstance().get(Calendar.DAY_OF_WEEK) }
    private lateinit var todayShopLocation: Location
    private lateinit var kmToShop: String
    private var favoriteShops: MutableList<FavoriteShopModel?> = mutableListOf()

    private fun getDistanceToShop() {
        MapUtil.getUserCurrentLocation(
            getApplication<Application>().applicationContext,
            ::calculateMeterFromCoordinates
        )
    }

    private suspend fun calculateMeterFromCoordinates(
        currentUserLatitude: Double,
        currentUserLongitude: Double,
    ) {
        val meter = MapUtil.calculateMeterFromCoordinates(
            todayShopLocation.latitude,
            todayShopLocation.longitude,
            currentUserLatitude,
            currentUserLongitude
        )
        kmToShop = if (meter < 3000) "${meter}m" else "${meter / 1000}km"
    }

    fun getShopInfo(shopId: Int, userKey: String) {
        viewModelScope.launch {
            val shop = getShopInfoUseCase(shopId)
            val favoriteShops = getUserFavoriteShopsUseCase(userKey)
            viewModelScope.launch {
                shop.zip(favoriteShops) { shop, favoriteShops ->
                    if (shop is UiState.Success && favoriteShops is UiState.Success) {
                        shop.value?.let { shop ->
                            todayShopLocation = shop.location[today]
                            favoriteShops.value?.toList()
                                ?.let { this@ShopViewModel.favoriteShops.addAll(it) }
                            getDistanceToShop()
                            val shopModel = shop.copy(
                                isFavoriteShop = favoriteShops.value?.any { shopId == it.shopId }
                                    ?: false,
                                kmToShop = kmToShop,
                                offDate = getOffDate(shop)
                            )
                            _uiState.value = UiState.Success(shopModel)
                        }
                    } else if (shop is UiState.Error) {
                        _uiState.value = shop
                    } else if (favoriteShops is UiState.Error) {
                        _uiState.value = favoriteShops
                    } else {
                    }
                }.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5000),
                    UiState.Loading
                )
            }
        }
    }

    private fun getOffDate(shop: ShopModel): Int {
        var offDate = -1
        shop.location.forEachIndexed { index, location ->
            if (location.latitude == 0.0 && location.longitude == 0.0) {
                offDate = index
            }
        }
        return offDate
    }

    fun isTodayOff(): Boolean {
        return (uiState.value as UiState.Success<ShopModel?>).value!!.offDate == today
    }

    fun addFavoriteShop(shop: ShopModel, userKey: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val favoriteShop = FavoriteShop(shop.id, shop.name)
            addFavoriteShopUseCase(
                userKey = userKey,
                favoriteShopsSize = favoriteShops.size,
                favoriteShop = favoriteShop
            ).collect { uiState ->
                if (uiState is UiState.Success) {
                    _uiState.value = UiState.Success(
                        (_uiState.value as UiState.Success).value?.copy(
                            isFavoriteShop = true
                        )
                    )
                }
            }
        }
    }

    fun deleteFavoriteShop(shop: ShopModel, userKey: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val indexInFavorites = favoriteShops.indexOfFirst { it?.shopId == shop.id }
            deleteFavoriteShopUseCase(
                userKey = userKey,
                indexInFavorites = indexInFavorites
            ).collect { uiState ->
                if (uiState is UiState.Success) {
                    _uiState.value = UiState.Success(
                        (_uiState.value as UiState.Success).value?.copy(
                            isFavoriteShop = false
                        )
                    )
                }
            }
        }
    }

}