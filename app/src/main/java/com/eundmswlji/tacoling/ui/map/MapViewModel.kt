package com.eundmswlji.tacoling.ui.map

import androidx.lifecycle.ViewModel
import com.eundmswlji.tacoling.data.repository.JusoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel :ViewModel() {
    @Inject lateinit var jusoRepository: JusoRepository

}