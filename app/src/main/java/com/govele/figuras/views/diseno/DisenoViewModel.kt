package com.govele.figuras.views.diseno

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.govele.figuras.domain.model.Figura
import com.govele.figuras.domain.model.Punto
import com.govele.figuras.domain.usecase.GetUltimaFiguraUseCase
import com.govele.figuras.domain.usecase.SaveFiguraUseCase
import com.govele.figuras.domain.usecase.SetFiguraAsUltimaUseCase
import com.govele.figuras.domain.usecases.GetFiguraByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DisenoState(
    val figura: Figura? = null,
    val isEditing: Boolean = false,
    val selectedPointIndex: Int = -1,
    val isSaved: Boolean = false
)

@HiltViewModel
class DisenoViewModel @Inject constructor(
    private val saveFiguraUseCase: SaveFiguraUseCase,
    private val getUltimaFiguraUseCase: GetUltimaFiguraUseCase,
    private val setFiguraAsUltimaUseCase: SetFiguraAsUltimaUseCase,
    private val getFigurasByID : GetFiguraByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DisenoState())
    val state: StateFlow<DisenoState> = _state.asStateFlow()

    fun setFigura(figura: Figura) {
        // Asegurar que sea custom y tenga ID único
        val figuraCustom = figura.copy(
            esCustom = true,
            id = "custom_${System.currentTimeMillis()}" // ID único
        )
        println("🔄 VIEWMODEL: Configurando figura custom: ${figuraCustom.nombre}")
        println("🔄 VIEWMODEL: ID: ${figuraCustom.id}")
        println("🔄 VIEWMODEL: Puntos: ${figuraCustom.puntos}")
        _state.update { it.copy(figura = figuraCustom) }
    }

    fun loadLastSavedFigura() {
        viewModelScope.launch {
            val lastFigura = getUltimaFiguraUseCase()
            if (lastFigura != null) {
                _state.value = DisenoState(figura = lastFigura)
            }
        }
    }

    fun selectPoint(index: Int) {
        _state.update { it.copy(selectedPointIndex = index, isEditing = true) }
    }

    fun loadFiguraById(id: Int) {
        viewModelScope.launch {
            println("🟡 DISENO_VIEWMODEL: Cargando figura con ID: $id")
            val figura = getFigurasByID(id)
            if (figura != null) {
                println("🟢 DISENO_VIEWMODEL: Figura cargada: ${figura.nombre}")
                _state.update { it.copy(figura = figura.copy(esCustom = true)) }
            }
        }
    }

    fun updatePointPosition(index: Int, newPoint: Punto) {
        val currentFigura = _state.value.figura ?: return

        val updatedPoints = currentFigura.puntos.toMutableList()
        if (index in updatedPoints.indices) {
            updatedPoints[index] = newPoint
            val updatedFigura = currentFigura.copy(puntos = updatedPoints)
            updatedFigura.puntos.forEachIndexed { i, punto ->
                println("🔄   Punto $i: (${punto.x}, ${punto.y})")
            }
            _state.update { it.copy(figura = updatedFigura) }
        }
    }

    fun saveFigura() {
        viewModelScope.launch {
            val currentFigura = _state.value.figura ?: return@launch

            val figuraParaGuardar = if (!currentFigura.esCustom) {
                currentFigura.copy(
                    id = "custom_${System.currentTimeMillis()}",
                    nombre = "${currentFigura.nombre} (Modificada)",
                    esCustom = true
                )
            } else {
                currentFigura
            }

            try {
                saveFiguraUseCase(figuraParaGuardar)
                println("✅ FIGURA GUARDADA EXITOSAMENTE")

                _state.update { it.copy(figura = figuraParaGuardar, isSaved = true) }

                figuraParaGuardar.id.toIntOrNull()?.let { id ->
                    setFiguraAsUltimaUseCase(id)
                }

            } catch (e: Exception) {
                println("❌ ERROR GUARDANDO: ${e.message}")
            }
        }
    }

    fun clearSaveState() {
        _state.update { it.copy(isSaved = false) }
    }

    fun stopEditing() {
        _state.update { it.copy(isEditing = false, selectedPointIndex = -1) }
    }
}