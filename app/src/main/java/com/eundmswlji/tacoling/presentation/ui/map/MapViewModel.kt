package com.eundmswlji.tacoling.presentation.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.data.model.Coordinates
import com.eundmswlji.tacoling.domain.model.ShopsModel
import com.eundmswlji.tacoling.domain.status.UiState
import com.eundmswlji.tacoling.domain.usecase.shop.GetAllShopsUseCase
import com.eundmswlji.tacoling.presentation.util.MapUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAllShopsUseCase: GetAllShopsUseCase,
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

    private val todayDate by lazy { Calendar.getInstance().get(Calendar.DAY_OF_WEEK) }
    private val _currentUserCoordinates = MutableLiveData<Coordinates>()
    val currentUserCoordinates: LiveData<Coordinates> = _currentUserCoordinates
    private val _currentUserAddress = MutableLiveData<String>()
    val currentUserAddress: LiveData<String> = _currentUserAddress
    private val _uiState: MutableStateFlow<UiState<List<MapPOIItem>?>> =
        MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState<List<MapPOIItem>?>> = _uiState
    private val _shops = MutableLiveData<ShopsModel>()


    val selectedDate = MutableLiveData<Int>().apply { value = setTodayDateRadioButton() }


    fun setCurrentUserCoordinate(latitude: Double, longitude: Double) {
        _currentUserCoordinates.value = Coordinates(latitude, longitude)
    }


    fun setCurrentUserAddress(address: String) {
        _currentUserAddress.value = address
    }

    fun getAllShopsIn3Km(getOnlyZeroWaste: Boolean = false) = viewModelScope.launch {
        _uiState.value = UiState.Loading
        getAllShopsUseCase(getOnlyZeroWaste)
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading
            )
            .onEach { uiState ->
                if (uiState is UiState.Success) {
                    uiState.value?.shops?.filter { shop ->
                        val meter = MapUtil.calculateMeterFromCoordinates(
                            currentUserCoordinates.value!!.latitude,
                            currentUserCoordinates.value!!.longitude,
                            shop.location[todayDate].latitude,
                            shop.location[todayDate].longitude,
                        )
                        meter < 3000
                    }?.map {
                        MapUtil.getMapPOIItem(
                            it.name,
                            it.location[todayDate].latitude,
                            it.location[todayDate].longitude,
                            it.doZeroWaste,
                            it.id
                        )
                    }.apply {
                        _uiState.value = UiState.Success(this)
                    }
                }
                else if (uiState is UiState.Error) {
                    _uiState.value = UiState.Error("근처 3km 이내에 가게가 없습니다.")
                }

            }
            .collect()
    }

    fun getMapPOIItemByDate(date: Int) {
        _uiState.value = UiState.Loading
        _shops.value?.shops?.map { shop ->
            MapUtil.getMapPOIItem(
                shop.name,
                shop.location[date].latitude,
                shop.location[date].longitude,
                shop.doZeroWaste,
                shop.id
            )
        }.apply {
            _uiState.value = UiState.Success(this)
        }
    }

}


