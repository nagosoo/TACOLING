package com.eundmswlji.tacoling.ui.shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    private val userRepository: UserRepository
) : ViewModel() {

    private val _toastHelper = MutableLiveData<Event<String>>()
    val toastHelper: LiveData<Event<String>> = _toastHelper

    private val _shopInfo = MutableLiveData<ShopXX>()
    val shopInfo: LiveData<ShopXX> = _shopInfo

    private val _isLikedShop = MutableLiveData<Boolean>()
    val isLikedShop: LiveData<Boolean> = _isLikedShop

    private val _kmToShop = MutableLiveData<Int>()
    val kmToShop: LiveData<Int> = _kmToShop

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

    fun getKmToShop(currentLocation: Pair<Double?, Double?>) =
        viewModelScope.launch {
            val km = if (currentLocation.first == null || currentLocation.second == null) 0
            else MapUtil.getKmFromHereToShop(
                currentLocation.first!!,
                todayLocation.latitude,
                currentLocation.second!!,
                todayLocation.longitude
            )
            _kmToShop.postValue(km)
        }


    fun getShopInfo(shopId: Int) {
        viewModelScope.launch {
            val response = shopRepository.getShopInfo(shopId)
            if (response.isSuccessful) {
                response.body()?.let { shopInfo ->
                    _shopInfo.postValue(shopInfo)
                    todayLocation = shopInfo.location[todayDate]
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