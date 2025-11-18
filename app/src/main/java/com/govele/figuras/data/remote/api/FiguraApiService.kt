package com.govele.figuras.data.remote.api

import com.govele.figuras.data.remote.dto.ApiFigura
import retrofit2.Response
import retrofit2.http.GET

interface FiguraApiService {
    @GET("polygons")
    suspend fun getFiguras() : Response<List<ApiFigura>>

}