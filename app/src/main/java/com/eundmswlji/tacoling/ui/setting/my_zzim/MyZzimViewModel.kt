package com.eundmswlji.tacoling.ui.setting.my_zzim

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eundmswlji.tacoling.data.model.Shop

class MyZzimViewModel : ViewModel() {
    private val _myZzimList = MutableLiveData<MutableList<Shop>>()
    val myZzimList: LiveData<MutableList<Shop>> = _myZzimList

    fun setMyZzimList() {
        _myZzimList.value = mutableListOf(Shop(id = 0, name = "타코왕", menu = listOf()))
    }

    fun removeMyZzimList(shop: Shop) {
        _myZzimList.value = mutableListOf()
    }
}