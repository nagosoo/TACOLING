package com.eundmswlji.tacoling.ui.map

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.eundmswlji.tacoling.BuildConfig
import com.eundmswlji.tacoling.Event
import com.eundmswlji.tacoling.Util.toast
import com.eundmswlji.tacoling.data.model.Document
import com.eundmswlji.tacoling.data.repository.JusoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val jusoRepository: JusoRepository
) : ViewModel() {

    private val _currentAddress  = MutableLiveData<Event<String>>()
    val currentAddress  : LiveData<Event<String>> = _currentAddress

    private val _toastEvent = MutableLiveData<Event<String>>()
    val toastEvent : LiveData<Event<String>> = _toastEvent

    suspend fun getJuso(query: String): Flow<PagingData<Document>> {
        return jusoRepository.getJuso(query).cachedIn(viewModelScope)
    }

    fun getJusoFromGeoCord(mapPoint: MapPoint?, activity: FragmentActivity?) {
        mapPoint?.let {
            val currentMapPoint = MapPoint.mapPointWithGeoCoord(mapPoint.mapPointGeoCoord.latitude, mapPoint.mapPointGeoCoord.longitude)
            MapReverseGeoCoder(BuildConfig.appKey, currentMapPoint, object : MapReverseGeoCoder.ReverseGeoCodingResultListener {
                override fun onReverseGeoCoderFoundAddress(p0: MapReverseGeoCoder?, address: String) {
                    _currentAddress.value = Event(address)
                }

                override fun onReverseGeoCoderFailedToFindAddress(p0: MapReverseGeoCoder?) {
                    _toastEvent.value = Event("주소를 찾을 수 없습니다.")
                }
            }, activity).startFindingAddress()
        }
    }

    fun setCurrentJuso(address:String){
        _currentAddress.value = Event(address)
    }

}