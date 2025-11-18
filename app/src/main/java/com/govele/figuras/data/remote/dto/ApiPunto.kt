package com.govele.figuras.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiPunto(@SerializedName("x")
                    val x: Float,
                    @SerializedName("y")
                    val y: Float)
