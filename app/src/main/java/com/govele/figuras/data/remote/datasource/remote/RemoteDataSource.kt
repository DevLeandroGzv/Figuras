package com.govele.figuras.data.datasource.remote

import com.govele.figuras.data.remote.api.FiguraApiService
import com.govele.figuras.data.remote.dto.ApiFigura
import javax.inject.Inject

class RemoteDataSource @Inject constructor(    private val apiService: FiguraApiService
) {
    suspend fun getFiguras(): List<ApiFigura> {
        val response = apiService.getFiguras()

        if (response.isSuccessful) {
            val figuras = response.body() ?: emptyList()
            figuras.forEach { figura ->
                println("   - ${figura.nombre}: ${figura.puntos.size} puntos")
            }
            return figuras
        } else {
            throw Exception("Error fetching figuras: ${response.code()}")
        }
    }
}