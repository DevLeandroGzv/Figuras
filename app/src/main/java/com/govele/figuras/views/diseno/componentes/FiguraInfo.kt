package com.govele.figuras.views.diseno.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.govele.figuras.domain.model.Figura

@Composable
fun FiguraInfo(
    figura: Figura?,
    isEditing: Boolean,
    puntoSeleccionado: Int,
    isSaved: Boolean,
    onClearSaveState: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Estado de guardado
            if (isSaved) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    onClearSaveState()
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Figura guardada correctamente")
                }
            }

            // Información de la figura
            figura?.let { fig ->
                Text(
                    text = fig.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Puntos: ${fig.puntos.size}")

                if (isEditing && puntoSeleccionado != -1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Editando punto ${puntoSeleccionado + 1}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Coordenadas: (${"%.2f".format(fig.puntos[puntoSeleccionado].x)}, ${"%.2f".format(fig.puntos[puntoSeleccionado].y)})",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } ?: Text("No hay figura seleccionada")
        }
    }
}