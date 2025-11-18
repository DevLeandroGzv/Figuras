// domain/repository/FiguraRepository.kt
package com.govele.figuras.domain.repository

import com.govele.figuras.domain.model.Figura
import kotlinx.coroutines.flow.Flow

interface FiguraRepository {
    fun getFigurasPredisenadas(): Flow<List<Figura>>
    fun getFigurasPersonalizadas(): Flow<List<Figura>>
    suspend fun getFiguraById(id: Int): Figura?
    suspend fun saveFigura(figura: Figura)
    suspend fun refreshFiguras()
    suspend fun getUltimaFigura(): Figura?
    suspend fun setFiguraAsUltima(id: Int)
}