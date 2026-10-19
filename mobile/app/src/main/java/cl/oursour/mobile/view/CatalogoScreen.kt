package cl.oursour.mobile.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Liquor
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import cl.oursour.mobile.model.Producto
import cl.oursour.mobile.util.formatearPrecio
import cl.oursour.mobile.view.components.CarritoBoton
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.viewmodel.CarritoViewModel
import kotlinx.coroutines.launch

// Catálogo de productos Our Sour
@Composable
fun CatalogoScreen(
    onVolver: () -> Unit,
    onCarrito: () -> Unit,
    onEscanearQr: () -> Unit,
    onVerProducto: (Int) -> Unit,
    carritoViewModel: CarritoViewModel = viewModel()
) {
    var busqueda by remember { mutableStateOf("") }
    val productos = CatalogoRepository.productos.filter { it.nombre.contains(busqueda.trim(), ignoreCase = true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            OurSourTopBar(titulo = "Productos Our Sour", onVolver = onVolver, acciones = {
                IconButton(onClick = onEscanearQr) {
                    Icon(Icons.Filled.QrCodeScanner, contentDescription = "Escanear QR")
                }
                CarritoBoton(onClick = onCarrito)
            })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    placeholder = { Text("Buscar productos...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(productos, key = { it.id }) { producto ->
                TarjetaProducto(
                    producto = producto,
                    onClick = { onVerProducto(producto.id) },
                    onAgregar = {
                        carritoViewModel.agregar(producto)
                        scope.launch { snackbarHostState.showSnackbar("${producto.nombre} agregado al carro") }
                    }
                )
            }
        }
    }
}

@Composable
private fun TarjetaProducto(producto: Producto, onClick: () -> Unit, onAgregar: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Liquor, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    producto.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
                Text(formatearPrecio(producto.precio), style = MaterialTheme.typography.titleMedium)
                Text(
                    if (producto.stock > 0) "Stock: ${producto.stock}" else "Sin stock",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (producto.stock > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                )
            }
            FilledTonalIconButton(onClick = onAgregar, enabled = producto.stock > 0) {
                Icon(Icons.Filled.AddShoppingCart, contentDescription = "Agregar al carro")
            }
        }
    }
}
