package cl.oursour.mobile.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.data.repository.CatalogoRepository
import cl.oursour.mobile.model.RecetaRecomendada
import cl.oursour.mobile.model.TipoReceta
import cl.oursour.mobile.ui.theme.Verde
import cl.oursour.mobile.view.components.CarritoBoton
import cl.oursour.mobile.view.components.EstadoVacio
import cl.oursour.mobile.view.components.IconoReceta
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.viewmodel.RecetasViewModel

// Pantalla 3 del wireframe: Recetas que puedes hacer
@Composable
fun RecetasScreen(
    ingredientesSeleccionados: Set<Int>,
    onVolver: () -> Unit,
    onCarrito: () -> Unit,
    onVerReceta: (Int) -> Unit,
    recetasViewModel: RecetasViewModel = viewModel()
) {
    // Se ejecuta el motor de sugerencias al entrar a la pantalla
    LaunchedEffect(ingredientesSeleccionados) {
        recetasViewModel.cargar(ingredientesSeleccionados)
    }
    val mostrarCoincidencia = ingredientesSeleccionados.isNotEmpty()
    val recetas = recetasViewModel.recetasDelTipo()

    Scaffold(
        topBar = {
            OurSourTopBar(
                titulo = if (mostrarCoincidencia) "Recetas que puedes hacer" else "Todas las recetas",
                onVolver = onVolver,
                acciones = { CarritoBoton(onClick = onCarrito) }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = recetasViewModel.tipoSeleccionado.ordinal) {
                TipoReceta.entries.forEach { tipo ->
                    Tab(
                        selected = recetasViewModel.tipoSeleccionado == tipo,
                        onClick = { recetasViewModel.cambiarTipo(tipo) },
                        text = { Text(tipo.etiqueta) }
                    )
                }
            }

            if (recetas.isEmpty()) {
                EstadoVacio(
                    icono = Icons.Filled.SearchOff,
                    titulo = "Sin recetas compatibles",
                    mensaje = "No encontramos ${recetasViewModel.tipoSeleccionado.etiqueta.lowercase()} con tus ingredientes. Prueba agregando más ingredientes a tu despensa."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (mostrarCoincidencia) {
                        item {
                            Text(
                                "Ordenadas por coincidencia con tus ${ingredientesSeleccionados.size} ingrediente(s)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    items(recetas, key = { it.receta.id }) { recomendada ->
                        TarjetaReceta(
                            recomendada = recomendada,
                            mostrarCoincidencia = mostrarCoincidencia,
                            onClick = { onVerReceta(recomendada.receta.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TarjetaReceta(
    recomendada: RecetaRecomendada,
    mostrarCoincidencia: Boolean,
    onClick: () -> Unit
) {
    val receta = recomendada.receta
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoReceta(receta.tipo)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(receta.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        receta.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (mostrarCoincidencia) {
                    Text(
                        "${recomendada.porcentaje}%",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (recomendada.porcentaje == 100) Verde else MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            if (mostrarCoincidencia) {
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { recomendada.porcentaje / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = if (recomendada.porcentaje == 100) Verde else MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                receta.ingredientes.take(4).forEach { item ->
                    CatalogoRepository.buscarIngrediente(item.ingredienteId)?.let {
                        SuggestionChip(onClick = onClick, label = { Text(it.nombre, style = MaterialTheme.typography.labelSmall) })
                    }
                }
            }

            if (mostrarCoincidencia) {
                Text(
                    if (recomendada.faltantes.isEmpty()) "¡Tienes todos los ingredientes!"
                    else "Te falta: " + recomendada.faltantes.joinToString(", ") { it.nombre },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (recomendada.faltantes.isEmpty()) Verde else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
