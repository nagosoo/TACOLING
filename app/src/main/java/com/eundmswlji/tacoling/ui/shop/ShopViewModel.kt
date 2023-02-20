package com.eundmswlji.tacoling.ui.shop

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.eundmswlji.tacoling.Event
import com.eundmswlji.tacoling.data.model.LikedShopX
import com.eundmswlji.tacoling.data.model.Location
import com.eundmswlji.tacoling.data.model.ShopXX
import com.eundmswlji.tacoling.data.repository.shop.ShopRepository
import com.eundmswlji.tacoling.data.repository.user.UserRepository
import com.eundmswlji.tacoling.util.MapUtil
import com.eundmswlji.tacoling.util.Util.todayDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val shopRepository: ShopRepository,
    private val userRepository: UserRepository,
    app: Application
) : AndroidViewModel(app) {

    private val _toastHelper = MutableLiveData<Event<String>>()
    val toastHelper: LiveData<Event<String>> = _toastHelper

    private val _shopInfo = MutableLiveData<ShopXX>()
    val shopInfo: LiveData<ShopXX> = _shopInfo

    private val _isLikedShop = MutableLiveData<Boolean>()
    val isLikedShop: LiveData<Boolean> = _isLikedShop

    private val _kmToShop = MutableLiveData<String>()
    val kmToShop: LiveData<String> = _kmToShop

    lateinit var todayLocation: Location
    private var userId: String? = null


    init {
        if (userId == null) {
            viewModelScope.launch {
                userRepository.getUserId()?.let {
                    userId = it
                }
            }
        }
    }

    private fun getDistanceToShop() {
        MapUtil.getCurrentLocation(
            getApplication<Application>().applicationContext,
            ::getDistanceFromHereToShop
        )
    }

    private fun getDistanceFromHereToShop(
        currentLatitude: Double,
        currentLongitude: Double
    ) {
        viewModelScope.launch {
            val meter = MapUtil.distanceCalculate(
                todayLocation.latitude,
                todayLocation.longitude,
                currentLatitude,
                currentLongitude
            )

            val km = if (meter < 3000) "${meter}m" else "${meter / 1000}km"
            _kmToShop.postValue(km)
        }
    }

    fun getShopInfo(shopId: Int) {
        viewModelScope.launch {
            val response = shopRepository.getShopInfo(shopId)
            if (response.isSuccessful) {
                response.body()?.let { shopInfo ->
                    _shopInfo.postValue(shopInfo)
                    todayLocation = shopInfo.location[todayDate]
                    getDistanceToShop()
                }
            } else {
                _toastHelper.postValue(Event(response.errorBody()?.string() ?: "error"))
            }
        }
    }

    fun getShopInLikedList(shopId: Int) {
        userId?.let { userId ->
            viewModelScope.launch {
                val isLikedShop = userRepository.isShopInLikedList(userId, shopId)
                _isLikedShop.postValue(isLikedShop)
            }
        }
    }

    fun addLikedShop(shopId: Int) {
        userId?.let { userId ->
            viewModelScope.launch {
                val shop = LikedShopX(shopId, shopInfo.value!!.name)
                val response = userRepository.addLikedShop(userId = userId, body = shop)
                if (response.isSuccessful) {
                    toggleLikedShop(true)
                } else {
                    _toastHelper.postValue(Event(response.errorBody()?.string() ?: "error"))
                }
            }
        }
    }

    fun removeMyLikedList() {
        userId?.let { userId ->
            viewModelScope.launch {
                val response = userRepository.deleteLikedShop(userId, shopInfo.value!!.id)
                if (response.isSuccessful) {
                    toggleLikedShop(false)
                } else {
                    _toastHelper.postValue(Event(response.errorBody()?.string() ?: "error"))
                }
            }
        }
    }

    private fun toggleLikedShop(isLiked: Boolean) = _isLikedShop.postValue(isLiked)

}