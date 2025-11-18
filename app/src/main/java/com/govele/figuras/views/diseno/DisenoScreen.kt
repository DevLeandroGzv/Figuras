package com.govele.figuras.views.diseno


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.govele.figuras.domain.model.Figura

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisenoScreen(
    figura: Figura? = null,
    onBack: () -> Unit,
    viewModel: DisenoViewModel = hiltViewModel()
) {

    if (figura != null) {
        LaunchedEffect(figura) {
            viewModel.setFigura(figura)
        }
    } else {

        LaunchedEffect(Unit) {
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
                    imageVector = Icons.Default.Save,
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
        }
    }
}