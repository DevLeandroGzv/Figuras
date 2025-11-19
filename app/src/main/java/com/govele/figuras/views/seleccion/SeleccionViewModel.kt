package com.govele.figuras.views.seleccion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govele.figuras.domain.model.Figura
import com.govele.figuras.domain.model.Punto
import com.govele.figuras.domain.usecase.GetFigurasPredisenadasUseCase
import com.govele.figuras.domain.usecases.GetFigurasPersonalizadasUseCase
import com.govele.figuras.domain.usecases.RefreshFigurasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SeleccionState(
    val figuras: List<Figura> = emptyList(),
    val figuraspredisenadas: List<Figura> = emptyList(),
    val figurasPersonalizadas: List<Figura> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFigura: Figura? = null,
    val polygonLados: Int = 3,
    val escala: Float = 1.0f
){
    val todaslasfiguras: List<Figura>
        get() = figuraspredisenadas + figurasPersonalizadas
}

@HiltViewModel
class SeleccionViewModel @Inject constructor(
    private val getFigurasPredisenadasUseCase: GetFigurasPredisenadasUseCase,
    private val refreshFigurasUseCase: RefreshFigurasUseCase,
    private val getFigurasPersonalizadasUseCase: GetFigurasPersonalizadasUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SeleccionState())
    val state: StateFlow<SeleccionState> = _state.asStateFlow()



    init {
        loadAllFiguras()
        refreshFromNetwork()
    }

    private fun loadAllFiguras() {

        viewModelScope.launch {
            // Combinar ambas fuentes en una sola lista
            getFigurasPredisenadasUseCase()
                .combine(getFigurasPersonalizadasUseCase()) { predisenadas, personalizadas ->
                    val todasLasFiguras = predisenadas + personalizadas
                    println("📋 LISTA UNIFICADA: ${todasLasFiguras.size} figuras total")
                    println("   - Prediseñadas: ${predisenadas.size}")
                    println("   - Personalizadas: ${personalizadas.size}")

                    // Mostrar todas las figuras para debug
                    todasLasFiguras.forEachIndexed { index, figura ->
                        println("   $index. ${figura.nombre} (${if (figura.esCustom) "Personalizada" else "Prediseñada"})")
                    }

                    _state.value.copy(
                        figuras = todasLasFiguras,  // ← LISTA UNIFICADA
                        figuraspredisenadas = predisenadas,
                        figurasPersonalizadas = personalizadas
                    )
                }
                .collect { newState ->
                    _state.update { newState }
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
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "No se pudieron cargar figuras nuevas. Usando cache local."
                    )
                }
            }
        }
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

    fun generatePolygon(lados: Int) {
        viewModelScope.launch {
            val polygon = Figura.generatePolygon(lados)
            _state.update { currentState ->
                currentState.copy(
                    selectedFigura = polygon,
                    polygonLados = lados
                )
            }
        }
    }

    fun selectFigura(figura: Figura) {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(selectedFigura = figura)
            }
        }
    }

    fun loadFigurasPersonalizadas() {
        viewModelScope.launch {
            println("📂 Cargando figuras personalizadas...")
            getFigurasPersonalizadasUseCase().collect { figuras ->
                println("📂 Figuras personalizadas encontradas: ${figuras.size}")
                figuras.forEach { figura ->
                    println("   - ${figura.nombre} (ID: ${figura.id}) - Puntos: ${figura.puntos}")
                }
                // Agregar a la lista existente o mostrar en una sección separada
            }
        }
    }


}