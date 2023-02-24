package com.eundmswlji.tacoling.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.eundmswlji.tacoling.Event
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.data.model.Document
import com.eundmswlji.tacoling.data.repository.address.AddressRepository
import com.eundmswlji.tacoling.util.Util.todayDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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

    private val _showZeroWasteShop = MutableLiveData<Boolean>(true)
    val showZeroWasteShop: LiveData<Boolean> = _showZeroWasteShop

    val _selectedDate = MutableLiveData<Int>().apply { value = setTodayDate() }

    fun toggleZeroWasteShop() {
        _showZeroWasteShop.value = !_showZeroWasteShop.value!!
    }

    fun setCurrentAddress(address: String) {
        _currentAddress.value = address
    }

}