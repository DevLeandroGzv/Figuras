package com.govele.figuras.views.seleccion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.govele.figuras.domain.model.Figura

@Composable
fun SeleccionScreen(
    onNavigateToDiseno: (Figura) -> Unit,
    viewModel: SeleccionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Seleccionar Figura",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        PolygonControls(
            lados = state.polygonLados,
            onLadosChange = { lados -> viewModel.generatePolygon(lados) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ScaleControl(
            escala = state.escala,
            onEscalaChange = { escala -> viewModel.updateScale(escala) }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.getSelectedFiguraWithScale()?.let { figura ->
                    onNavigateToDiseno(figura)
                }
            },
            enabled = state.selectedFigura != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Continuar al Diseño")
        }
        FigurasList(
            figuras = state.figuras,
            figuraSeleccionada = state.selectedFigura,
            onFiguraSeleccionada = { figura -> viewModel.selectFigura(figura) },
            isLoading = state.isLoading
        )
        if (state.figurasPersonalizadas.isNotEmpty()) {
            Text(
                text = "Mis Figuras Guardadas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            FigurasList(
                figuras = state.figurasPersonalizadas,
                figuraSeleccionada = state.selectedFigura,
                onFiguraSeleccionada = { figura -> viewModel.selectFigura(figura) },
                isLoading = state.isLoading

            )
        }
    }
}

@Composable
private fun PolygonControls(
    lados: Int,
    onLadosChange: (Int) -> Unit
) {
    var ladosInput by remember { mutableStateOf(lados.toString()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Generar Polígono",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = ladosInput,
                    onValueChange = { ladosInput = it },
                    label = { Text("Lados (3-12)") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val ladosValue = ladosInput.toIntOrNull() ?: 3
                        if (ladosValue in 3..12) {
                            onLadosChange(ladosValue)
                        } else {
                            ladosInput = "3"
                        }
                    }
                ) {
                    Text("Generar")
                }
            }
        }
    }
}

@Composable
private fun ScaleControl(
    escala: Float,
    onEscalaChange: (Float) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Escala: ${"%.2f".format(escala)}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = escala,
                onValueChange = onEscalaChange,
                valueRange = 0.5f..3.0f,
                steps = 24
            )
        }
    }
}

@Composable
private fun FigurasList(
    figuras: List<Figura>,
    figuraSeleccionada: Figura?,
    onFiguraSeleccionada: (Figura) -> Unit,
    isLoading: Boolean
) {
    Card(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Figuras Disponibles",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(figuras) { figura ->
                    FiguraItem(
                        figura = figura,
                        isSelected = figura.id == figuraSeleccionada?.id,
                        onClick = {
                            onFiguraSeleccionada(figura)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FiguraItem(
    figura: Figura,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = {
            onClick()
        },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = figura.nombre,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "${figura.puntos.size} puntos",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}