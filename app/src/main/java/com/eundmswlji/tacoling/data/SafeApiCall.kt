package com.eundmswlji.tacoling.data

import android.util.Log
import com.eundmswlji.tacoling.domain.status.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response

fun <T> safeApiCall(apiCall: suspend () -> Response<T?>): Flow<UiState<T?>> = flow {
    try {
        val result = apiCall()
        if (result.isSuccessful) {
            emit(UiState.Success(result.body()))
        } else {
            Log.e("SafeApiCall", "safeApiCall: ${result.errorBody()}")
            emit(UiState.Error("오류가 발생 했습니다. 다시 시도해주세요"))
        }
    } catch (e: HttpException) {
        emit(UiState.Error("인터넷이 연결되어있는지 확인해주세요."))
    } catch (e: Exception) {
        Log.e("SafeApiCall", "safeApiCall: ${e.message}")
        emit(UiState.Error("오류가 발생 했습니다. 다시 시도해주세요."))
    }
}