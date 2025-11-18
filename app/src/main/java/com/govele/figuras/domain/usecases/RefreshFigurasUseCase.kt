package com.govele.figuras.domain.usecases


import com.govele.figuras.domain.repository.FiguraRepository
import javax.inject.Inject

class RefreshFigurasUseCase @Inject constructor(
    private val repository: FiguraRepository
) {
    suspend operator fun invoke() {
        repository.refreshFiguras()
    }
}