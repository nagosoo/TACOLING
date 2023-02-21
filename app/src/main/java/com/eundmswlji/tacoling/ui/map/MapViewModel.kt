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

    private val _searchedAddress = MutableStateFlow<PagingData<Document>>(PagingData.empty())
    val searchedAddress: StateFlow<PagingData<Document>> = _searchedAddress.asStateFlow()

    private val _toastEvent = MutableLiveData<Event<String>>()
    val toastEvent: LiveData<Event<String>> = _toastEvent

    private val _showZeroWasteShop = MutableLiveData<Boolean>(true)
    val showZeroWasteShop: LiveData<Boolean> = _showZeroWasteShop

    val _selectedDate = MutableLiveData<Int>().apply { value = setTodayDate() }
    fun toggleZeroWasteShop() {
        _showZeroWasteShop.value = !_showZeroWasteShop.value!!
    }

    fun getAddress(query: String) {
        viewModelScope.launch {
            addressRepository.getAddress(query)
                .cachedIn(viewModelScope)
                .collectLatest { _searchedAddress.value = it }
        }
    }

//    fun getAddressFromGeoCord(mapPoint: MapPoint?, activity: FragmentActivity?) {
//        mapPoint?.let {
//            val currentMapPoint = MapPoint.mapPointWithGeoCoord(
//                mapPoint.mapPointGeoCoord.latitude,
//                mapPoint.mapPointGeoCoord.longitude
//            )
//            MapReverseGeoCoder(
//                BuildConfig.appKey,
//                currentMapPoint,
//                object : MapReverseGeoCoder.ReverseGeoCodingResultListener {
//                    override fun onReverseGeoCoderFoundAddress(
//                        p0: MapReverseGeoCoder?,
//                        address: String
//                    ) {
//                        _currentAddress.value = Event(address)
//                    }
//
//                    override fun onReverseGeoCoderFailedToFindAddress(p0: MapReverseGeoCoder?) {
//                        _toastEvent.value = Event("주소를 찾을 수 없습니다.")
//                    }
//                },
//                activity
//            ).startFindingAddress()
//        }
//    }

    fun setCurrentAddress(address: String) {
        //    _currentAddress.value = Event(address)
    }

}