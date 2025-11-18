package com.govele.figuras.domain.usecases

import com.govele.figuras.domain.model.Figura
import com.govele.figuras.domain.repository.FiguraRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFigurasPersonalizadasUseCase @Inject constructor(
    private val repository: FiguraRepository
) {
    operator fun invoke(): Flow<List<Figura>> {
        return repository.getFigurasPersonalizadas()
    }
}