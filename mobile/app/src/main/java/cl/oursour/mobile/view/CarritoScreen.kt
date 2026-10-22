package cl.oursour.mobile.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.data.repository.CatalogoRepository
import cl.oursour.mobile.ui.theme.Verde
import cl.oursour.mobile.util.ReglasDespacho
import cl.oursour.mobile.util.formatearPrecio
import cl.oursour.mobile.view.components.ImagenProducto
import cl.oursour.mobile.view.components.BotonPrincipal
import cl.oursour.mobile.view.components.EstadoVacio
import cl.oursour.mobile.view.components.FilaResumen
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.view.components.SelectorCantidad
import cl.oursour.mobile.viewmodel.CarritoViewModel

// Pantalla 5 del wireframe: Mi Carro
@Composable
fun CarritoScreen(
    onVolver: () -> Unit,
    onIrACatalogo: () -> Unit,
    onContinuarCompra: () -> Unit,
    carritoViewModel: CarritoViewModel = viewModel()
) {
    val items by carritoViewModel.items.collectAsState(initial = emptyList())
    val subtotal = carritoViewModel.calcularSubtotal(items)
    val despacho = ReglasDespacho.calcular(subtotal, esRetiroEnTienda = false)
    var confirmarVaciar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            OurSourTopBar(titulo = "Mi Carro", onVolver = onVolver, acciones = {
                if (items.isNotEmpty()) {
                    IconButton(onClick = { confirmarVaciar = true }) {
                        Icon(Icons.Filled.DeleteSweep, contentDescription = "Vaciar carro")
                    }
                }
            })
        },
        bottomBar = {
            if (items.isNotEmpty()) {
                Surface(shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        FilaResumen("Subtotal", formatearPrecio(subtotal))
                        FilaResumen("Despacho (estimado)", if (despacho == 0) "Gratis" else formatearPrecio(despacho))
                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                        FilaResumen("Total", formatearPrecio(subtotal + despacho), destacado = true)
                        Spacer(Modifier.height(12.dp))
                        BotonPrincipal(
                            texto = "Continuar compra",
                            icono = Icons.AutoMirrored.Filled.ArrowForward,
                            onClick = onContinuarCompra
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (items.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.RemoveShoppingCart,
                titulo = "Tu carro está vacío",
                mensaje = "Agrega productos Our Sour desde una receta o desde el catálogo.",
                modifier = Modifier.padding(padding)
            ) {
                TextButton(onClick = onIrACatalogo) { Text("Ver productos") }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Aviso de envío gratis
            item {
                val faltaParaGratis = ReglasDespacho.MONTO_ENVIO_GRATIS - subtotal
                Text(
                    if (faltaParaGratis > 0) "Agrega ${formatearPrecio(faltaParaGratis)} más y obtén despacho gratis"
                    else "¡Tienes despacho gratis!",
                    color = if (faltaParaGratis > 0) MaterialTheme.colorScheme.tertiary else Verde,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(items, key = { it.productoId }) { item ->
                val stock = CatalogoRepository.buscarProducto(item.productoId)?.stock ?: item.cantidad
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        ImagenProducto(productoId = item.productoId, modifier = Modifier.size(56.dp), esquinas = 10.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${formatearPrecio(item.precio)} c/u",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(formatearPrecio(item.precio * item.cantidad), fontWeight = FontWeight.Bold)
                        }
                        SelectorCantidad(
                            cantidad = item.cantidad,
                            onCambiar = { carritoViewModel.cambiarCantidad(item, it) },
                            maximo = stock
                        )
                        IconButton(onClick = { carritoViewModel.eliminar(item) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            }
        }
    }

    if (confirmarVaciar) {
        AlertDialog(
            onDismissRequest = { confirmarVaciar = false },
            title = { Text("Vaciar carro") },
            text = { Text("¿Quieres quitar todos los productos del carro?") },
            confirmButton = {
                TextButton(onClick = {
                    carritoViewModel.vaciar()
                    confirmarVaciar = false
                }) { Text("Vaciar") }
            },
            dismissButton = { TextButton(onClick = { confirmarVaciar = false }) { Text("Cancelar") } }
        )
    }
}
