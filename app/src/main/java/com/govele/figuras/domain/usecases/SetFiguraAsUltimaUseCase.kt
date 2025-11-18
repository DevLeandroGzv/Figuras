// domain/usecase/SetFiguraAsUltimaUseCase.kt
package com.govele.figuras.domain.usecase

import com.govele.figuras.domain.repository.FiguraRepository
import javax.inject.Inject

class SetFiguraAsUltimaUseCase @Inject constructor(
    private val repository: FiguraRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.setFiguraAsUltima(id)
    }
}