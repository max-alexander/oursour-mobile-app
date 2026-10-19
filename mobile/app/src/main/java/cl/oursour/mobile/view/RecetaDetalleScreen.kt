package cl.oursour.mobile.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.data.repository.CatalogoRepository
import cl.oursour.mobile.ui.theme.Rojo
import cl.oursour.mobile.ui.theme.Verde
import cl.oursour.mobile.util.formatearPrecio
import cl.oursour.mobile.view.components.BotonDorado
import cl.oursour.mobile.view.components.CarritoBoton
import cl.oursour.mobile.view.components.EstadoVacio
import cl.oursour.mobile.view.components.IconoReceta
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.view.components.QrImagen
import cl.oursour.mobile.viewmodel.CarritoViewModel
import cl.oursour.mobile.viewmodel.RecetaDetalleViewModel
import kotlinx.coroutines.launch

// Pantalla 4 del wireframe: Detalle de receta
@Composable
fun RecetaDetalleScreen(
    recetaId: Int,
    ingredientesSeleccionados: Set<Int>,
    onVolver: () -> Unit,
    onCarrito: () -> Unit,
    onVerProducto: (Int) -> Unit,
    detalleViewModel: RecetaDetalleViewModel = viewModel(),
    carritoViewModel: CarritoViewModel = viewModel()
) {
    LaunchedEffect(recetaId) {
        detalleViewModel.cargar(recetaId, ingredientesSeleccionados)
    }
    val receta = detalleViewModel.receta
    val producto = detalleViewModel.productoAsociado()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var mostrarQr by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            OurSourTopBar(titulo = "", onVolver = onVolver, acciones = {
                IconButton(onClick = { mostrarQr = true }) {
                    Icon(Icons.Filled.QrCode2, contentDescription = "Ver QR de la receta")
                }
                CarritoBoton(onClick = onCarrito)
            })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (producto != null) {
                Surface(shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        BotonDorado(
                            texto = if (producto.stock > 0) "Añadir al carro  ${formatearPrecio(producto.precio)}" else "Producto sin stock",
                            icono = Icons.Filled.AddShoppingCart,
                            habilitado = producto.stock > 0,
                            onClick = {
                                carritoViewModel.agregar(producto)
                                scope.launch { snackbarHostState.showSnackbar("${producto.nombre} agregado al carro") }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (receta == null) {
            EstadoVacio(
                icono = Icons.Filled.Cancel,
                titulo = "Receta no encontrada",
                mensaje = "Puede que haya sido eliminada del catálogo.",
                modifier = Modifier.padding(padding)
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoReceta(receta.tipo, tamano = 64.dp)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(receta.titulo, style = MaterialTheme.typography.headlineSmall)
                    Text(receta.tipo.etiqueta.dropLast(1), color = MaterialTheme.colorScheme.tertiary)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(receta.descripcion, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(24.dp))
            Text("Ingredientes necesarios", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            // ✓ si el usuario lo tiene, ✗ si le falta
            receta.ingredientes.forEach { item ->
                val ingrediente = CatalogoRepository.buscarIngrediente(item.ingredienteId)
                val lotiene = detalleViewModel.tieneIngrediente(item.ingredienteId)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (lotiene) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                        contentDescription = if (lotiene) "Disponible" else "Falta",
                        tint = if (lotiene) Verde else Rojo,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(ingrediente?.nombre ?: "Ingrediente eliminado", modifier = Modifier.weight(1f))
                    Text(item.cantidad, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("Pasos de preparación", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            receta.pasos.forEachIndexed { indice, paso ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        "${indice + 1}.",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.width(28.dp)
                    )
                    Text(paso)
                }
            }

            Spacer(Modifier.height(24.dp))

            // ¿Falta el producto Our Sour? -> invitar a comprarlo. Si no -> ¡a preparar!
            if (producto != null) {
                val falta = detalleViewModel.faltaProductoOurSour()
                Card(
                    onClick = { onVerProducto(producto.id) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, if (falta) Rojo else Verde),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (falta) {
                            Text("Get the perfect mix", style = MaterialTheme.typography.titleMedium, color = Rojo)
                            Text("Te falta ${producto.nombre} para preparar esta receta.")
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.SentimentSatisfied, contentDescription = null, tint = Verde)
                                Spacer(Modifier.width(8.dp))
                                Text("¡Ya tienes el producto Our Sour!", style = MaterialTheme.typography.titleMedium, color = Verde)
                            }
                            Text("Puedes preparar la receta. ¿Quieres reponer stock?")
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${producto.nombre} · ${formatearPrecio(producto.precio)}",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Toca para ver el producto",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (mostrarQr && receta != null) {
        AlertDialog(
            onDismissRequest = { mostrarQr = false },
            title = { Text("Comparte esta receta") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    QrImagen(contenido = "OURSOUR:RECETA:${receta.id}", tamano = 220.dp)
                    Spacer(Modifier.height(8.dp))
                    Text("Escanéalo desde otra app Our Sour para abrir \"${receta.titulo}\"")
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarQr = false }) { Text("Cerrar") }
            }
        )
    }
}
