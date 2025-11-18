// domain/usecase/GetUltimaFiguraUseCase.kt
package com.govele.figuras.domain.usecase

import com.govele.figuras.domain.model.Figura
import com.govele.figuras.domain.repository.FiguraRepository
import javax.inject.Inject

class GetUltimaFiguraUseCase @Inject constructor(
    private val repository: FiguraRepository
) {
    suspend operator fun invoke(): Figura? {
        return repository.getUltimaFigura()
    }
}