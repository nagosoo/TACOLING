package com.eundmswlji.tacoling.ui.setting.my_liked

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eundmswlji.tacoling.data.model.Shop

class MyLikedViewModel : ViewModel() {
    private val _myLikedList = MutableLiveData<MutableList<Shop>>()
    val myLikedList: LiveData<MutableList<Shop>> = _myLikedList

    fun setMyLikedList() {
        _myLikedList.value = mutableListOf(Shop(id = 0, name = "타코왕", menu = listOf()))
    }

    fun removeMyLikedList(shop: Shop) {
        _myLikedList.value = mutableListOf()
    }
}