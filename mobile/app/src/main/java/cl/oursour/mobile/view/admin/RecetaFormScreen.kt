package cl.oursour.mobile.view.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.data.repository.CatalogoRepository
import cl.oursour.mobile.model.CategoriaIngrediente
import cl.oursour.mobile.model.TipoReceta
import cl.oursour.mobile.view.components.BotonPrincipal
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.viewmodel.RecetaFormViewModel

// Formulario para crear (recetaId = 0) o editar una receta
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecetaFormScreen(
    recetaId: Int,
    onVolver: () -> Unit,
    formViewModel: RecetaFormViewModel = viewModel()
) {
    LaunchedEffect(recetaId) { formViewModel.cargar(recetaId) }

    Scaffold(
        topBar = { OurSourTopBar(titulo = if (recetaId == 0) "Nueva receta" else "Editar receta", onVolver = onVolver) },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    formViewModel.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    BotonPrincipal(texto = "Guardar receta", icono = Icons.Filled.Save, onClick = { formViewModel.guardar(onVolver) })
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            OutlinedTextField(
                value = formViewModel.titulo,
                onValueChange = { formViewModel.cambiarTitulo(it) },
                label = { Text("Título") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = formViewModel.descripcion,
                onValueChange = { formViewModel.cambiarDescripcion(it) },
                label = { Text("Descripción corta") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Text("Tipo", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TipoReceta.entries.forEach { tipo ->
                    FilterChip(
                        selected = formViewModel.tipo == tipo,
                        onClick = { formViewModel.cambiarTipo(tipo) },
                        label = { Text(tipo.etiqueta.dropLast(1)) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Producto Our Sour asociado", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CatalogoRepository.productos.forEach { producto ->
                    FilterChip(
                        selected = formViewModel.productoId == producto.id,
                        onClick = { formViewModel.cambiarProducto(producto.id) },
                        label = { Text(producto.nombre) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Pasos de preparación (uno por línea)", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = formViewModel.pasos,
                onValueChange = { formViewModel.cambiarPasos(it) },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Text(
                "Ingredientes y cantidades (${formViewModel.cantidades.size} seleccionados)",
                style = MaterialTheme.typography.titleMedium
            )
            CategoriaIngrediente.entries.forEach { categoria ->
                val ingredientes = CatalogoRepository.ingredientes.filter { it.categoria == categoria }
                if (ingredientes.isNotEmpty()) {
                    Text(
                        categoria.etiqueta,
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    ingredientes.forEach { ingrediente ->
                        val seleccionado = formViewModel.cantidades.containsKey(ingrediente.id)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = seleccionado,
                                onCheckedChange = { formViewModel.alternarIngrediente(ingrediente.id) }
                            )
                            Text(ingrediente.nombre, modifier = Modifier.weight(1f))
                            if (seleccionado) {
                                Spacer(Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = formViewModel.cantidades[ingrediente.id].orEmpty(),
                                    onValueChange = { formViewModel.cambiarCantidad(ingrediente.id, it) },
                                    placeholder = { Text("Ej: 2 oz") },
                                    singleLine = true,
                                    modifier = Modifier.width(120.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
