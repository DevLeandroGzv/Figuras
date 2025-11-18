package com.govele.figuras.views.seleccion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govele.figuras.domain.model.Figura
import com.govele.figuras.domain.usecase.GetFigurasPredisenadasUseCase
import com.govele.figuras.domain.usecase.RefreshFigurasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SeleccionState(
    val figuras: List<Figura> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFigura: Figura? = null,
    val polygonLados: Int = 3,
    val escala: Float = 1.0f
)

@HiltViewModel
class SeleccionViewModel @Inject constructor(
    private val getFigurasPredisenadasUseCase: GetFigurasPredisenadasUseCase,
    private val refreshFigurasUseCase: RefreshFigurasUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SeleccionState())
    val state: StateFlow<SeleccionState> = _state.asStateFlow()

    init {
        loadFiguras()
        refreshFromNetwork()
    }

    private fun loadFiguras() {
        viewModelScope.launch {
            getFigurasPredisenadasUseCase().collect { figuras ->
                _state.update { it.copy(figuras = figuras) }
            }
        }
    }

    private fun refreshFromNetwork() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                refreshFigurasUseCase()
                _state.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    error = "No se pudieron cargar figuras nuevas. Usando cache local."
                ) }
            }
        }
    }

    fun selectFigura(figura: Figura) {
        _state.update { it.copy(selectedFigura = figura) }
    }

    fun generatePolygon(lados: Int) {
        val polygon = Figura.generatePolygon(lados)
        _state.update { it.copy(selectedFigura = polygon, polygonLados = lados) }
    }

    fun updateScale(escala: Float) {
        _state.update { it.copy(escala = escala) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun getSelectedFiguraWithScale(): Figura? {
        return _state.value.selectedFigura?.copy(escala = _state.value.escala)
    }
}