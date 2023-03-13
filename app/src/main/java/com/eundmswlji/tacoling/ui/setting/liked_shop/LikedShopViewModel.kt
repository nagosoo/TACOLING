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

    lateinit var cachedLikedList: MutableList<LikedShopX>

    private val _likedList = MutableStateFlow<List<LikedShopX>>(emptyList())
    val likedList: StateFlow<List<LikedShopX>> = _likedList

    val listUpdated: MutableLiveData<Int> = MutableLiveData()

    private val _toastHelper = MutableLiveData<Event<String>>()
    val toastHelper: LiveData<Event<String>> = _toastHelper

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

    fun getLikedShops() {
        userId?.let { userId ->
            viewModelScope.launch {
                userRepository.getUserLikedShops(userId).apply {
                    stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
                }.collect { list ->
                    _likedList.value = list
                    cachedLikedList = list.toMutableList()
                }
            }
        }
    }

    fun removeMyLikedList(shopId: Int, position: Int) {
        userId?.let { userId ->
            viewModelScope.launch {
                val response = userRepository.deleteLikedShop(userId, shopId)
                if (response.isSuccessful) {
                    cachedLikedList.removeAt(position)
                } else {
                    _toastHelper.postValue(Event(response.errorBody()?.string() ?: "error"))
                }
            }
        }
    }

    //되돌리기 눌렀을 때
    fun addMyLikedList(shopIndex: Int, shopId: Int, shopName: String) {
        userId?.let { userId ->
            viewModelScope.launch {
                val shop = LikedShopX(shopId, shopName)
                val response =
                    userRepository.addLikedShop(userId, shopIndex + 1, shop)
                if (response.isSuccessful) {
                    // 그 포자션만 notify
                    cachedLikedList.add(shopIndex, shop)
                    listUpdated.postValue(shopIndex)
                } else {
                    _toastHelper.postValue(Event(response.errorBody()?.string() ?: "error"))
                }
            }
        }
    }

}