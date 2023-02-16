package com.eundmswlji.tacoling.ui.setting.liked_shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eundmswlji.tacoling.Event
import com.eundmswlji.tacoling.data.model.LikedShopX
import com.eundmswlji.tacoling.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikedShopViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _myLikedList = MutableStateFlow<List<LikedShopX>>(emptyList())
    val myLikedList: StateFlow<List<LikedShopX>> = _myLikedList

    private val _toastHelper = MutableLiveData<Event<String>>()
    val toastHelper: LiveData<Event<String>> = _toastHelper

    private var userId: String? = null

    init {
        viewModelScope.launch {
            userRepository.getUserId()?.let {
                userId = it
            }
        }
    }

    fun getLikedShops() {
        userId?.let { userId ->
            viewModelScope.launch {
                userRepository.getUserLikedShops(userId).apply {
                    stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
                }.collect {
                    _myLikedList.value = it
                }
            }
        }
    }

    fun removeMyLikedList(shopIndex: Int) {
        userId?.let { userId ->
            viewModelScope.launch {
                val response = userRepository.deleteLikedShop(userId, shopIndex)
                if (response.isSuccessful) {

                } else {
                    _toastHelper.postValue(Event(response.errorBody()?.string() ?: "error"))
                }
            }
        }
    }

    fun addMyLikedList(shopIndex: Int, shopId: Int, shopName: String) {
        userId?.let { userId ->
            viewModelScope.launch {
                val response =
                    userRepository.addLikedShop(userId, shopIndex, LikedShopX(shopId, shopName))
                if (response.isSuccessful) {
                //왜 되돌리기가 안대지     getLikedShops()
                } else {
                    _toastHelper.postValue(Event(response.errorBody()?.string() ?: "error"))
                }
            }
        }
    }

}