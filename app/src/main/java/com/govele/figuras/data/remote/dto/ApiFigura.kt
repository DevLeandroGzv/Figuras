package com.govele.figuras.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiFigura(@SerializedName("name")
                     val nombre: String,
                     @SerializedName("points")
                     val puntos: List<ApiPunto>)
