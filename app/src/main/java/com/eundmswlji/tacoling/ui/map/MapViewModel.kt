package com.eundmswlji.tacoling.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.eundmswlji.tacoling.data.model.Document
import com.eundmswlji.tacoling.data.repository.JusoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val jusoRepository: JusoRepository
):ViewModel() {

    suspend fun getJuso(query:String): Flow<PagingData<Document>>{
        return jusoRepository.getJuso(query).cachedIn(viewModelScope)
    }
}