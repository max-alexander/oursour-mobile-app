package cl.oursour.mobile.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.data.repository.CatalogoRepository
import cl.oursour.mobile.util.formatearPrecio
import cl.oursour.mobile.view.components.ImagenProducto
import cl.oursour.mobile.view.components.BotonDorado
import cl.oursour.mobile.view.components.CarritoBoton
import cl.oursour.mobile.view.components.EstadoVacio
import cl.oursour.mobile.view.components.IconoReceta
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.view.components.QrImagen
import cl.oursour.mobile.view.components.SelectorCantidad
import cl.oursour.mobile.viewmodel.CarritoViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductoDetalleScreen(
    productoId: Int,
    onVolver: () -> Unit,
    onCarrito: () -> Unit,
    onVerReceta: (Int) -> Unit,
    carritoViewModel: CarritoViewModel = viewModel()
) {
    val producto = CatalogoRepository.buscarProducto(productoId)
    var cantidad by remember { mutableIntStateOf(1) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            OurSourTopBar(titulo = "", onVolver = onVolver, acciones = { CarritoBoton(onClick = onCarrito) })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (producto != null) {
                Surface(shadowElevation = 8.dp) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        SelectorCantidad(
                            cantidad = cantidad,
                            onCambiar = { cantidad = it.coerceIn(1, producto.stock.coerceAtLeast(1)) },
                            maximo = producto.stock
                        )
                        Spacer(Modifier.size(12.dp))
                        BotonDorado(
                            texto = formatearPrecio(producto.precio * cantidad),
                            icono = Icons.Filled.AddShoppingCart,
                            habilitado = producto.stock > 0,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                carritoViewModel.agregar(producto, cantidad)
                                scope.launch { snackbarHostState.showSnackbar("$cantidad x ${producto.nombre} agregado al carro") }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        if (producto == null) {
            EstadoVacio(
                icono = Icons.Filled.Cancel,
                titulo = "Producto no encontrado",
                mensaje = "Puede que haya sido eliminado del catálogo.",
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
            ImagenProducto(
                productoId = producto.id,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                esquinas = 20.dp
            )
            Spacer(Modifier.height(16.dp))
            Text(producto.nombre, style = MaterialTheme.typography.headlineSmall)
            Text(formatearPrecio(producto.precio), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.tertiary)
            Text(
                if (producto.stock > 0) "${producto.stock} unidades disponibles" else "Sin stock",
                color = if (producto.stock > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(12.dp))
            Text(producto.descripcion)

            val recetas = CatalogoRepository.recetasConProducto(producto.id)
            if (recetas.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                Text("Recetas con este producto", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                    recetas.forEachIndexed { indice, receta ->
                        ListItem(
                            headlineContent = { Text(receta.titulo) },
                            supportingContent = { Text(receta.tipo.etiqueta.dropLast(1)) },
                            leadingContent = { IconoReceta(receta.tipo, tamano = 40.dp) },
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                            modifier = Modifier.clickable { onVerReceta(receta.id) }
                        )
                        if (indice < recetas.lastIndex) HorizontalDivider()
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("Código QR del producto", style = MaterialTheme.typography.titleLarge)
            Text(
                "Este código viene impreso en el envase. Escanéalo desde la app para abrir este producto.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                QrImagen(contenido = "OURSOUR:PRODUCTO:${producto.id}")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
