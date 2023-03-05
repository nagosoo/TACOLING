package com.eundmswlji.tacoling.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eundmswlji.tacoling.Event
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.data.model.Coordinates
import com.eundmswlji.tacoling.data.model.MapViewShop
import com.eundmswlji.tacoling.data.model.ShopItem
import com.eundmswlji.tacoling.data.repository.shop.ShopRepository
import com.eundmswlji.tacoling.util.MapUtil
import com.eundmswlji.tacoling.util.Util.todayDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val shopRepository: ShopRepository
) : ViewModel() {

    private fun setTodayDateRadioButton(): Int {
        return when (todayDate) {
            0 -> R.id.rbSun
            1 -> R.id.rbMon
            2 -> R.id.rbThu
            3 -> R.id.rbWed
            4 -> R.id.rbThr
            5 -> R.id.rbFri
            else -> R.id.rbSat
        }
    }

    fun setTodayDateIndex(): Int {
        return when (selectedDate.value) {
            R.id.rbSun -> 0
            R.id.rbMon -> 1
            R.id.rbThu -> 2
            R.id.rbWed -> 3
            R.id.rbThr -> 4
            R.id.rbFri -> 5
            else -> 6
        }
    }

    private val _toastEvent = MutableLiveData<Event<String>>()
    val toastEvent: LiveData<Event<String>> = _toastEvent

    private val _currentAddress = MutableLiveData<String>().apply { value = "주소 검색 중" }
    val currentAddress: LiveData<String> = _currentAddress

    private val _currentGeoCord = MutableLiveData<Coordinates>()
    val currentGeoCord: LiveData<Coordinates> = _currentGeoCord

    private val _showOnlyZeroWasteShop = MutableLiveData<Boolean>().apply { value = false }
    val showOnlyZeroWasteShop: LiveData<Boolean> = _showOnlyZeroWasteShop

    private val _rawShopList = MutableLiveData<List<ShopItem>>()
    val rawShopList: LiveData<List<ShopItem>> = _rawShopList

    private val _dailyShopList = MutableLiveData<List<MapViewShop>>().apply { value = emptyList() }
    val dailyShopList: LiveData<List<MapViewShop>> = _dailyShopList

    private val _dailyShopIn3KmList =
        MutableLiveData<List<MapPOIItem>>().apply { value = emptyList() }
    val dailyShopIn3KmList: LiveData<List<MapPOIItem>> = _dailyShopIn3KmList

    val selectedDate = MutableLiveData<Int>().apply { value = setTodayDateRadioButton() }

    fun toggleZeroWasteShop() {
        _showOnlyZeroWasteShop.value = !_showOnlyZeroWasteShop.value!!
    }

    fun setCurrentAddress(address: String) {
        _currentAddress.value = address
    }

    fun setCurrentGeoCord(latitude: Double, longitude: Double) {
        _currentGeoCord.value = Coordinates(latitude, longitude)
    }

    fun getShopList(zeroWaste: Boolean? = null) = viewModelScope.launch {
        val shopList = shopRepository.getShopList(zeroWaste)
        if (shopList.isNotEmpty()) {
            _rawShopList.postValue(shopList)
        } else {
            _toastEvent.value = Event("근처 3km이내에 타코야키 트럭이 없습니다.")
        }
    }

    fun getDailyShopList(date: Int = todayDate) {
        _rawShopList.value?.map { shop ->
            val location = shop.location[date]
            MapViewShop(
                id = shop.id,
                name = shop.name,
                location = location,
                zeroWaste = shop.zeroWaste
            )
        }?.let { shopList ->
            _dailyShopList.value = shopList
        }
    }

    fun getShopIn3Km() = viewModelScope.launch(Dispatchers.Default) {
        _dailyShopList.value?.filter { shop ->
            val meter = MapUtil.distanceCalculate(
                currentGeoCord.value!!.latitude,
                currentGeoCord.value!!.longitude,
                shop.location.latitude,
                shop.location.longitude
            )
            meter < 3000
        }?.let { dailyShopList ->
            if (currentGeoCord.value != null && dailyShopList.isEmpty()) {
                _toastEvent.postValue(Event("근처 3km이내에 타코야키 트럭이 없습니다."))
            }

            val list = mutableListOf<MapPOIItem>()
            dailyShopList.filter { it.location.longitude != 0.0 }.map { shop ->
                list.add(
                    MapUtil.getMapPOIItem(
                        shop.name,
                        shop.location.latitude,
                        shop.location.longitude,
                        shop.zeroWaste,
                        shop.id
                    )
                )
            }
            _dailyShopIn3KmList.postValue(list)
        }
    }

}

