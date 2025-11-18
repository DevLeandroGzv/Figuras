package com.govele.figuras.data.datasource.remote

import com.govele.figuras.data.remote.api.FiguraApiService
import com.govele.figuras.data.remote.dto.ApiFigura
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiService: FiguraApiService
) {
    suspend fun getFiguras(): List<ApiFigura> {
        val response = apiService.getFiguras()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        }
        throw Exception("Error capturando figuras: ${response.code()}")
    }
}