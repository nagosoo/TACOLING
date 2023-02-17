package com.eundmswlji.tacoling.ui.shop

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eundmswlji.tacoling.Event
import com.eundmswlji.tacoling.data.model.Location
import com.eundmswlji.tacoling.data.model.ShopXX
import com.eundmswlji.tacoling.data.repository.shop.ShopRepository
import com.eundmswlji.tacoling.util.Util.todayDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val shopRepository: ShopRepository
) : ViewModel() {

    private val _toastHelper = MutableLiveData<Event<String>>()
    val toastHelper: LiveData<Event<String>> = _toastHelper

    private val _shopInfo = MutableLiveData<ShopXX>()
    val shopInfo: LiveData<ShopXX> = _shopInfo

    lateinit var todayLocation : Location

    fun getShopInfo(shopId: Int) {
        viewModelScope.launch {
            val response = shopRepository.getShopInfo(shopId)
            if (response.isSuccessful) {
                _shopInfo.postValue(response.body())
                todayLocation = _shopInfo.value!!.location[todayDate]
            } else {
                _toastHelper.postValue(Event(response.errorBody()?.string() ?: "error"))
            }
        }
    }

}