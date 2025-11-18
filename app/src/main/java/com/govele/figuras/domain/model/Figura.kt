package com.govele.figuras.domain.model

import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow


data class Punto(
    val x: Float,
    val y: Float
)

data class Figura(
    val id: String,
    val nombre: String,
    val puntos: List<Punto>,
    val escala: Float = 1.0f,
    val esCustom: Boolean = false,
    val esUltima: Boolean = false
) {
    companion object {
        private val gson = Gson()

        fun puntosToJson(puntos: Flow<R>): String {
            return gson.toJson(puntos)
        }

        fun puntosFromJson(json: String): List<Punto> {
            return try {
                gson.fromJson(json, Array<Punto>::class.java).toList()
            } catch (e: Exception) {
                emptyList()
            }
        }

        fun generatePolygon(lados: Int): Figura {
            val puntos = mutableListOf<Punto>()
            val centroX = 0.5f
            val centroY = 0.5f
            val radio = 0.4f

            for (i in 0 until lados) {
                val angulo = 2.0 * Math.PI * i / lados
                val x = centroX + radio * Math.cos(angulo).toFloat()
                val y = centroY + radio * Math.sin(angulo).toFloat()
                puntos.add(Punto(x, y))
            }

            return Figura(
                id = "poligono_$lados",
                nombre = "Polígono $lados lados",
                puntos = puntos
            )
        }
    }

    fun getPuntosEscalados(): List<Punto> {
        return puntos.map { punto ->
            Punto(punto.x * escala, punto.y * escala)
        }
    }
}