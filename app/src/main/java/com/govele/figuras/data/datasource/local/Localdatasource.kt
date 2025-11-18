package com.govele.figuras.data.datasource.local

import com.govele.figuras.data.local.dao.FiguraDao
import com.govele.figuras.data.local.entity.FiguraEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val figuraDao: FiguraDao
) {
    fun getFigurasPredisenadas(): Flow<List<FiguraEntity>> {
        return figuraDao.getFigurasPredisenadas()
    }

    fun getFigurasPersonalizadas(): Flow<List<FiguraEntity>> {
        return figuraDao.getFigurasPersonalizadas()
    }

    suspend fun getFiguraById(id: Int): FiguraEntity? {
        return figuraDao.getFiguraById(id)
    }

    suspend fun insertFigura(figura: FiguraEntity) {
        figuraDao.insertFigura(figura)
    }

    suspend fun insertAllFiguras(figuras: List<FiguraEntity>) {
        figuraDao.insertAllFiguras(figuras)
    }

    suspend fun getUltimaFigura(): FiguraEntity? {
        return figuraDao.getUltimaFigura()
    }

    suspend fun clearUltimaFigura() {
        figuraDao.clearUltimaFigura()
    }

    suspend fun setFiguraAsUltima(id: Int) {
        figuraDao.setFiguraAsUltima(id)
    }
}