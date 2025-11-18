package com.govele.figuras.data.local.entity

import android.R
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.govele.figuras.domain.model.Figura
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "figuras")
data class FiguraEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val puntosJson: String,
    val escala: Float,
    val esCustom: Boolean = false,
    val esUltima: Boolean = false,
    val fechaCreacion: Long = System.currentTimeMillis()

) {
    fun toFigura(): Figura {
        return Figura(
            id = id.toString(),
            nombre = nombre,
            puntos = Figura.puntosFromJson(puntosJson),
            escala = escala,
            esCustom = esCustom,
            esUltima = esUltima
        )
    }

    companion object {
        fun fromFigura(figura: Figura): FiguraEntity {
            return FiguraEntity(
                id = figura.id.toIntOrNull() ?: 0,
                nombre = figura.nombre,
                puntosJson = Figura.puntosToJson(figura.puntos),
                escala = figura.escala,
                esCustom = figura.esCustom,
                esUltima = figura.esUltima,
                fechaCreacion = System.currentTimeMillis()
            )
        }
    }
}
