package cl.oursour.mobile.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.view.components.BotonPrincipal
import cl.oursour.mobile.view.components.EstadoVacio
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.viewmodel.DespensaViewModel

// Lista de ingredientes frecuentes guardados en la base de datos local
@Composable
fun MisIngredientesScreen(
    onVolver: () -> Unit,
    onIrADespensa: () -> Unit,
    onBuscarRecetas: (List<Int>) -> Unit,
    despensaViewModel: DespensaViewModel = viewModel()
) {
    val frecuentes by despensaViewModel.frecuentes.collectAsState(initial = emptyList())
    var confirmarBorrado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            OurSourTopBar(titulo = "Mis ingredientes", onVolver = onVolver, acciones = {
                if (frecuentes.isNotEmpty()) {
                    IconButton(onClick = { confirmarBorrado = true }) {
                        Icon(Icons.Filled.DeleteSweep, contentDescription = "Eliminar todos")
                    }
                }
            })
        },
        bottomBar = {
            if (frecuentes.isNotEmpty()) {
                Surface(shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        BotonPrincipal(
                            texto = "Buscar recetas con mis ingredientes",
                            icono = Icons.Filled.Search,
                            onClick = {
                                val ids = frecuentes.map { it.ingredienteId }
                                despensaViewModel.registrarBusqueda(ids)
                                onBuscarRecetas(ids)
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (frecuentes.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.StarBorder,
                titulo = "Aún no tienes ingredientes frecuentes",
                mensaje = "En Mi Despensa toca la estrella junto a un ingrediente para guardarlo aquí. Quedan guardados en tu teléfono.",
                modifier = Modifier.padding(padding)
            ) {
                TextButton(onClick = onIrADespensa) { Text("Ir a Mi Despensa") }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text(
                        "${frecuentes.size} ingrediente(s) guardados en tu dispositivo",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(frecuentes, key = { it.ingredienteId }) { frecuente ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        ListItem(
                            headlineContent = { Text(frecuente.nombre) },
                            supportingContent = { Text(frecuente.categoria) },
                            leadingContent = {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            },
                            trailingContent = {
                                IconButton(onClick = { despensaViewModel.eliminarFrecuente(frecuente.ingredienteId) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                                }
                            },
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        )
                    }
                }
            }
        }
    }

    if (confirmarBorrado) {
        AlertDialog(
            onDismissRequest = { confirmarBorrado = false },
            title = { Text("Eliminar todos") },
            text = { Text("¿Quieres eliminar todos tus ingredientes frecuentes?") },
            confirmButton = {
                TextButton(onClick = {
                    despensaViewModel.eliminarTodosLosFrecuentes()
                    confirmarBorrado = false
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarBorrado = false }) { Text("Cancelar") }
            }
        )
    }
}
