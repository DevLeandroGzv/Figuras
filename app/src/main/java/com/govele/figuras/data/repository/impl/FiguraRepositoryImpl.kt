package com.govele.figuras.data.repository.impl

import com.govele.figuras.R
import com.govele.figuras.data.datasource.local.LocalDataSource
import com.govele.figuras.data.datasource.remote.RemoteDataSource
import com.govele.figuras.data.local.entity.FiguraEntity
import com.govele.figuras.data.mapper.toDomain
import com.govele.figuras.data.mapper.toEntity
import com.govele.figuras.domain.model.Figura
import com.govele.figuras.domain.model.Punto
import com.govele.figuras.domain.repository.FiguraRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FiguraRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : FiguraRepository {

    override fun getFigurasPredisenadas(): Flow<List<Figura>> {
        return localDataSource.getFigurasPredisenadas().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFigurasPersonalizadas(): Flow<List<Figura>> {
        return localDataSource.getFigurasPersonalizadas().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getFiguraById(id: Int): Figura? {
        return localDataSource.getFiguraById(id)?.toDomain()
    }

    override suspend fun saveFigura(figura: Figura) {
        println("💾 REPOSITORY: Guardando figura ${figura.nombre}")
        println("💾 REPOSITORY: Puntos: ${figura.puntos}")
        println("💾 REPOSITORY: esCustom: ${figura.esCustom}")

        val entity = figura.toEntity()
        println("💾 REPOSITORY: Entity ID: ${entity.id}")
        println("💾 REPOSITORY: Entity puntosJson: ${entity.puntosJson}")

        localDataSource.insertFigura(entity)
        println("✅ REPOSITORY: Figura guardada en BD")
    }


    override suspend fun refreshFiguras() {
        try {
            val apiFiguras = remoteDataSource.getFiguras()
            val entities = apiFiguras.mapIndexed { index, apiFigura ->
                FiguraEntity(
                    id = 0,
                    nombre = apiFigura.nombre,
                    puntosJson = Figura.puntosToJson(
                        apiFigura.puntos.map { apiPunto ->
                            Punto(apiPunto.x, apiPunto.y)
                        }
                    ),
                    escala = 1.0f,
                    esCustom = false
                )
            }
            localDataSource.insertAllFiguras(entities)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getUltimaFigura(): Figura? {
        return localDataSource.getUltimaFigura()?.toDomain()
    }

    override suspend fun setFiguraAsUltima(id: Int) {
        localDataSource.clearUltimaFigura()
        localDataSource.setFiguraAsUltima(id)
    }
}