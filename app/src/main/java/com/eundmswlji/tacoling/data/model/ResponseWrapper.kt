package com.eundmswlji.tacoling.data.model

import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response

sealed class ApiResult<out T> {
    data class Success<out T>(val value: T): ApiResult<T>()
    object Empty: ApiResult<Nothing>()
    data class Error(val code: Int? = null, val exception: Throwable? = null): ApiResult<Nothing>()
}

fun <T> safeFlow(apiFunc: suspend () -> T): Flow<ApiResult<T>> = flow {
    try {
        emit(ApiResult.Success(apiFunc.invoke()))
    } catch (e: NullPointerException) {
        emit(ApiResult.Empty)
    } catch (e: HttpException) {
        emit(ApiResult.Error(code = e.code(), exception = e))
    } catch (e: Exception) {
        emit(ApiResult.Error(exception = e))
    }
}