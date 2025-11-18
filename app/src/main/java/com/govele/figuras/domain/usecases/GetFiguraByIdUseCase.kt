package com.govele.figuras.domain.usecases

import com.govele.figuras.domain.model.Figura
import com.govele.figuras.domain.repository.FiguraRepository
import javax.inject.Inject

class GetFiguraByIdUseCase @Inject constructor(
    private val repository: FiguraRepository
) {
    suspend operator fun invoke(id: Int): Figura? {
        return repository.getFiguraById(id)
    }
}
