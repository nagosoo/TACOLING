package com.eundmswlji.tacoling.ui.setting.liked_shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eundmswlji.tacoling.data.model.LikedShop
import com.eundmswlji.tacoling.data.model.Shop
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
    private val _myLikedList = MutableStateFlow<List<LikedShop?>>(emptyList())
    val myLikedList: StateFlow<List<LikedShop?>> = _myLikedList

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

    fun removeMyLikedList(shop: Shop) {

    }
}