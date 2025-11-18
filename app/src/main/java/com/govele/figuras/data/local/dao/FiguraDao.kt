package com.govele.figuras.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.govele.figuras.data.local.entity.FiguraEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface FiguraDao {
    @Query("SELECT * FROM figuras WHERE esCustom = 0 ORDER BY nombre")
    fun getFigurasPredisenadas(): Flow<List<FiguraEntity>>

    @Query("SELECT * FROM figuras WHERE esCustom = 1 ORDER BY fechaCreacion DESC")
    fun getFigurasPersonalizadas(): Flow<List<FiguraEntity>>

    @Query("SELECT * FROM figuras WHERE id = :id")
    suspend fun getFiguraById(id: Int): FiguraEntity?

    @Query("SELECT * FROM figuras WHERE esUltima = 1 LIMIT 1")
    suspend fun getUltimaFigura(): FiguraEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFigura(figura: FiguraEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFiguras(figuras: List<FiguraEntity>)

    @Query("DELETE FROM figuras WHERE id = :id")
    suspend fun deleteFigura(id: Int)

    @Query("UPDATE figuras SET esUltima = 0 WHERE esUltima = 1")
    suspend fun clearUltimaFigura()

    @Query("UPDATE figuras SET esUltima = 1 WHERE id = :id")
    suspend fun setFiguraAsUltima(id: Int)
}