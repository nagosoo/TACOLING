package com.eundmswlji.tacoling.data.source

import com.eundmswlji.tacoling.data.model.Juso
import com.eundmswlji.tacoling.retrofit.services.JusoService
import retrofit2.Response
import javax.inject.Inject

class JusoDataSourceImpl @Inject constructor(
    private val jusoService: JusoService
) : JusoDatasource {
    override suspend fun apiGetJuso(query: String, size: Int, page: Int): Response<Juso> = jusoService.apiGetJuso(query, size, page)
}