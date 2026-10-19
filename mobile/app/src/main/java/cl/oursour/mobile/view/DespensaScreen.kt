package cl.oursour.mobile.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.view.components.BotonPrincipal
import cl.oursour.mobile.view.components.CarritoBoton
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.viewmodel.DespensaViewModel

// Pantalla 2 del wireframe: Mi Despensa (selección de ingredientes disponibles)
@Composable
fun DespensaScreen(
    onVolver: () -> Unit,
    onCarrito: () -> Unit,
    onVerRecetas: (List<Int>) -> Unit,
    despensaViewModel: DespensaViewModel = viewModel()
) {
    val frecuentes by despensaViewModel.frecuentes.collectAsState(initial = emptyList())
    val idsFrecuentes = frecuentes.map { it.ingredienteId }.toSet()
    val categorias = despensaViewModel.ingredientesPorCategoria()
    val seleccionados = despensaViewModel.seleccionados

    Scaffold(
        topBar = {
            OurSourTopBar(titulo = "Mi Despensa", onVolver = onVolver, acciones = {
                CarritoBoton(onClick = onCarrito)
            })
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    BotonPrincipal(
                        texto = "Ver recetas compatibles (${seleccionados.size})",
                        habilitado = seleccionados.isNotEmpty(),
                        onClick = {
                            val ids = seleccionados.toList()
                            despensaViewModel.registrarBusqueda(ids)
                            onVerRecetas(ids)
                        }
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                OutlinedTextField(
                    value = despensaViewModel.busqueda,
                    onValueChange = { despensaViewModel.cambiarBusqueda(it) },
                    placeholder = { Text("Buscar ingredientes...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Accesos rápidos a los ingredientes frecuentes guardados en Room
            if (frecuentes.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mis frecuentes", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        TextButton(onClick = { despensaViewModel.seleccionarFrecuentes(frecuentes) }) {
                            Text("Seleccionar todos")
                        }
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(frecuentes) { frecuente ->
                            FilterChip(
                                selected = seleccionados.contains(frecuente.ingredienteId),
                                onClick = { despensaViewModel.alternarSeleccion(frecuente.ingredienteId) },
                                label = { Text(frecuente.nombre) },
                                leadingIcon = { Icon(Icons.Filled.Star, contentDescription = null) }
                            )
                        }
                    }
                }
            }

            if (seleccionados.isNotEmpty()) {
                item {
                    TextButton(onClick = { despensaViewModel.limpiarSeleccion() }) {
                        Text("Limpiar selección")
                    }
                }
            }

            categorias.forEach { (categoria, ingredientes) ->
                item {
                    Text(
                        categoria.etiqueta,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                    HorizontalDivider()
                }
                items(ingredientes, key = { it.id }) { ingrediente ->
                    val esFrecuente = ingrediente.id in idsFrecuentes
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { despensaViewModel.alternarSeleccion(ingrediente.id) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = seleccionados.contains(ingrediente.id),
                            onCheckedChange = { despensaViewModel.alternarSeleccion(ingrediente.id) }
                        )
                        Text(ingrediente.nombre, modifier = Modifier.weight(1f))
                        IconButton(onClick = { despensaViewModel.alternarFrecuente(ingrediente, esFrecuente) }) {
                            Icon(
                                if (esFrecuente) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = if (esFrecuente) "Quitar de frecuentes" else "Guardar como frecuente",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            if (categorias.isEmpty()) {
                item {
                    Text(
                        "No se encontraron ingredientes para \"${despensaViewModel.busqueda}\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}
