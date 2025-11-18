// domain/usecase/SaveFiguraUseCase.kt
package com.govele.figuras.domain.usecase

import com.govele.figuras.domain.model.Figura
import com.govele.figuras.domain.repository.FiguraRepository
import javax.inject.Inject

class SaveFiguraUseCase @Inject constructor(
    private val repository: FiguraRepository
) {
    suspend operator fun invoke(figura: Figura) {
        repository.saveFigura(figura)
    }
}