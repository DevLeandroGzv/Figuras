package com.govele.figuras.data.mapper

import com.govele.figuras.data.local.entity.FiguraEntity
import com.govele.figuras.domain.model.Figura

fun FiguraEntity.toDomain(): Figura {
    return Figura(
        id = this.id.toString(),
        nombre = this.nombre,
        puntos = Figura.puntosFromJson(this.puntosJson),
        escala = this.escala,
        esCustom = this.esCustom,
        esUltima = this.esUltima
    )
}

fun Figura.toEntity(): FiguraEntity {
    return FiguraEntity(
        id = this.id.toIntOrNull() ?: 0,
        nombre = this.nombre,
        puntosJson = Figura.puntosToJson(this.puntos),
        escala = this.escala,
        esCustom = this.esCustom,
        esUltima = this.esUltima
    )
}