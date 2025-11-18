package com.govele.figuras.views.diseno


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.govele.figuras.domain.model.Figura
import com.govele.figuras.ui.diseno.canvas.FiguraCanvas
import com.govele.figuras.views.diseno.componentes.FiguraInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisenoScreen(
    figuraId: Int? = null,
    onBack: () -> Unit,
    viewModel: DisenoViewModel = hiltViewModel()
) {

    LaunchedEffect(figuraId) {
        if (figuraId != null) {
            println("✅ DISENO_SCREEN: Cargando figura con ID: $figuraId")
            viewModel.loadFiguraById(figuraId)
        } else {
            println("✅ DISENO_SCREEN: Cargando última figura guardada")
            viewModel.loadLastSavedFigura()
        }
    }

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diseño de Figura") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.saveFigura() },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Guardar"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            FiguraCanvas(
                figura = state.figura,
                puntoSeleccionado = state.selectedPointIndex,
                onPointSelected = { index -> viewModel.selectPoint(index) },
                onPointMoved = { index, punto -> viewModel.updatePointPosition(index, punto) },
                isEditing = state.isEditing,
                onStopEditing = { viewModel.stopEditing() },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            FiguraInfo(
                figura = state.figura,
                isEditing = state.isEditing,
                puntoSeleccionado = state.selectedPointIndex,
                isSaved = state.isSaved,
                onClearSaveState = { viewModel.clearSaveState() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            if (state.figura != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "🎯 MODO EDICIÓN ACTIVO",
                            color = Color.Green,
                            style = MaterialTheme.typography.titleMedium
                        )

                        if (state.selectedPointIndex != -1) {
                            val punto = state.figura!!.puntos[state.selectedPointIndex]
                            Text(
                                "📍 Moviendo Punto ${state.selectedPointIndex}",
                                color = Color.Blue,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                "Coordenadas: (${"%.3f".format(punto.x)}, ${"%.3f".format(punto.y)})",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Text(
                                "👉 TOCA CUALQUIER PUNTO para moverlo",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}