package cl.oursour.mobile.view.admin

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.data.repository.CatalogoRepository
import cl.oursour.mobile.model.CategoriaIngrediente
import cl.oursour.mobile.model.Ingrediente
import cl.oursour.mobile.model.Producto
import cl.oursour.mobile.util.formatearPrecio
import cl.oursour.mobile.view.components.BotonPrincipal
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.viewmodel.AdminCatalogoViewModel

// Pantalla 7 del wireframe: Gestionar catálogo
@Composable
fun GestionCatalogoScreen(
    onVolver: () -> Unit,
    onNuevaReceta: () -> Unit,
    onEditarReceta: (Int) -> Unit,
    adminViewModel: AdminCatalogoViewModel = viewModel()
) {
    val pestanas = listOf("Recetas", "Ingredientes", "Productos")
    val snackbarHostState = remember { SnackbarHostState() }

    // Elemento que se está creando/editando en un diálogo (null = diálogo cerrado)
    var ingredienteEditando by remember { mutableStateOf<Ingrediente?>(null) }
    var productoEditando by remember { mutableStateOf<Producto?>(null) }
    // Texto y acción del diálogo de confirmación de borrado
    var confirmarEliminar by remember { mutableStateOf<Pair<String, () -> Unit>?>(null) }

    LaunchedEffect(adminViewModel.mensaje) {
        adminViewModel.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            adminViewModel.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = { OurSourTopBar(titulo = "Gestionar catálogo", onVolver = onVolver) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    BotonPrincipal(
                        texto = when (adminViewModel.pestana) {
                            0 -> "Añadir nueva receta"
                            1 -> "Añadir nuevo ingrediente"
                            else -> "Añadir nuevo producto"
                        },
                        icono = Icons.Filled.Add,
                        onClick = {
                            when (adminViewModel.pestana) {
                                0 -> onNuevaReceta()
                                1 -> ingredienteEditando = Ingrediente(0, "", CategoriaIngrediente.OTRO)
                                else -> productoEditando = Producto(0, "", "", 0, 0, 0)
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = adminViewModel.pestana) {
                pestanas.forEachIndexed { indice, titulo ->
                    Tab(
                        selected = adminViewModel.pestana == indice,
                        onClick = { adminViewModel.cambiarPestana(indice) },
                        text = { Text(titulo) }
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (adminViewModel.pestana) {
                    0 -> items(CatalogoRepository.recetas, key = { it.id }) { receta ->
                        FilaCatalogo(
                            titulo = receta.titulo,
                            detalle = "${receta.tipo.etiqueta.dropLast(1)} · ${receta.ingredientes.size} ingredientes",
                            onEditar = { onEditarReceta(receta.id) },
                            onEliminar = {
                                confirmarEliminar = "¿Eliminar la receta \"${receta.titulo}\"?" to {
                                    adminViewModel.eliminarReceta(receta.id)
                                }
                            }
                        )
                    }
                    1 -> items(CatalogoRepository.ingredientes, key = { it.id }) { ingrediente ->
                        FilaCatalogo(
                            titulo = ingrediente.nombre,
                            detalle = ingrediente.categoria.etiqueta,
                            onEditar = { ingredienteEditando = ingrediente },
                            onEliminar = {
                                confirmarEliminar = "¿Eliminar el ingrediente \"${ingrediente.nombre}\"?" to {
                                    adminViewModel.eliminarIngrediente(ingrediente.id)
                                }
                            }
                        )
                    }
                    else -> items(CatalogoRepository.productos, key = { it.id }) { producto ->
                        FilaCatalogo(
                            titulo = producto.nombre,
                            detalle = "${formatearPrecio(producto.precio)} · Stock: ${producto.stock}",
                            onEditar = { productoEditando = producto },
                            onEliminar = {
                                confirmarEliminar = "¿Eliminar el producto \"${producto.nombre}\"?" to {
                                    adminViewModel.eliminarProducto(producto.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    ingredienteEditando?.let { ingrediente ->
        DialogoIngrediente(
            ingrediente = ingrediente,
            onCerrar = { ingredienteEditando = null },
            onGuardar = { nombre, categoria ->
                val error = adminViewModel.guardarIngrediente(ingrediente.id, nombre, categoria)
                if (error == null) ingredienteEditando = null
                error
            }
        )
    }

    productoEditando?.let { producto ->
        DialogoProducto(
            producto = producto,
            onCerrar = { productoEditando = null },
            onGuardar = { nombre, descripcion, precio, stock ->
                val error = adminViewModel.guardarProducto(producto.id, nombre, descripcion, precio, stock)
                if (error == null) productoEditando = null
                error
            }
        )
    }

    confirmarEliminar?.let { (texto, accion) ->
        AlertDialog(
            onDismissRequest = { confirmarEliminar = null },
            title = { Text("Confirmar eliminación") },
            text = { Text(texto) },
            confirmButton = {
                TextButton(onClick = {
                    accion()
                    confirmarEliminar = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { confirmarEliminar = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun FilaCatalogo(titulo: String, detalle: String, onEditar: () -> Unit, onEliminar: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        ListItem(
            headlineContent = { Text(titulo) },
            supportingContent = { Text(detalle) },
            trailingContent = {
                Row {
                    IconButton(onClick = onEditar) { Icon(Icons.Filled.Edit, contentDescription = "Editar") }
                    IconButton(onClick = onEliminar) { Icon(Icons.Filled.Close, contentDescription = "Eliminar") }
                }
            },
            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        )
    }
}

// onGuardar devuelve un mensaje de error, o null si se guardó bien
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DialogoIngrediente(
    ingrediente: Ingrediente,
    onCerrar: () -> Unit,
    onGuardar: (String, CategoriaIngrediente) -> String?
) {
    var nombre by remember { mutableStateOf(ingrediente.nombre) }
    var categoria by remember { mutableStateOf(ingrediente.categoria) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(if (ingrediente.id == 0) "Nuevo ingrediente" else "Editar ingrediente") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; error = null },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Text("Categoría", style = MaterialTheme.typography.titleSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CategoriaIngrediente.entries.forEach {
                        FilterChip(selected = categoria == it, onClick = { categoria = it }, label = { Text(it.etiqueta) })
                    }
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = { TextButton(onClick = { error = onGuardar(nombre, categoria) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}

@Composable
private fun DialogoProducto(
    producto: Producto,
    onCerrar: () -> Unit,
    onGuardar: (String, String, String, String) -> String?
) {
    var nombre by remember { mutableStateOf(producto.nombre) }
    var descripcion by remember { mutableStateOf(producto.descripcion) }
    var precio by remember { mutableStateOf(if (producto.id == 0) "" else producto.precio.toString()) }
    var stock by remember { mutableStateOf(if (producto.id == 0) "" else producto.stock.toString()) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(if (producto.id == 0) "Nuevo producto" else "Editar producto") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it; error = null },
                    label = { Text("Nombre comercial") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it; error = null },
                    label = { Text("Descripción") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it.filter { c -> c.isDigit() }; error = null },
                    label = { Text("Precio (CLP)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it.filter { c -> c.isDigit() }; error = null },
                    label = { Text("Stock") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                if (producto.id == 0) {
                    Text(
                        "El producto también se agregará como ingrediente \"Producto Our Sour\".",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = { TextButton(onClick = { error = onGuardar(nombre, descripcion, precio, stock) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}
