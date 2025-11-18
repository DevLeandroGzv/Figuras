package com.govele.figuras.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.govele.figuras.domain.model.Punto

class Convertidores {
    private val gson = Gson()

    @TypeConverter
    fun puntosToJson(puntos: List<Punto>): String {
        return gson.toJson(puntos)
    }

    @TypeConverter
    fun jsonToPuntos(json: String): List<Punto> {
        val type = object : TypeToken<List<Punto>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}