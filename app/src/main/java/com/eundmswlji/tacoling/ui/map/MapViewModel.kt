package com.eundmswlji.tacoling.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eundmswlji.tacoling.Event
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.data.repository.address.AddressRepository
import com.eundmswlji.tacoling.util.Util.todayDate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {

    private fun setTodayDate(): Int {
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

    private val _toastEvent = MutableLiveData<Event<String>>()
    val toastEvent: LiveData<Event<String>> = _toastEvent

    private val _currentAddress = MutableLiveData<String>().apply { value = "주소 검색 중" }
    val currentAddress: LiveData<String> = _currentAddress

    private val _currentGeoCord = MutableLiveData<Map<String, Double>>()
    val currentGeoCord: LiveData<Map<String, Double>> = _currentGeoCord

    private val _showZeroWasteShop = MutableLiveData<Boolean>(true)
    val showZeroWasteShop: LiveData<Boolean> = _showZeroWasteShop

    val _selectedDate = MutableLiveData<Int>().apply { value = setTodayDate() }

    fun toggleZeroWasteShop() {
        _showZeroWasteShop.value = !_showZeroWasteShop.value!!
    }

    fun setCurrentAddress(address: String) {
        _currentAddress.value = address
    }

    fun setCurrentGeoCord(latitude: Double, longitude: Double) {
        _currentGeoCord.value = mapOf("latitude" to latitude, "longitude" to longitude)
    }

}