package cl.oursour.mobile.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.util.formatearPrecio
import cl.oursour.mobile.view.components.BotonDorado
import cl.oursour.mobile.view.components.FilaResumen
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.viewmodel.CarritoViewModel
import cl.oursour.mobile.viewmodel.CheckoutViewModel

// Resumen de la compra antes de pagar (checkout)
@Composable
fun ResumenScreen(
    checkoutViewModel: CheckoutViewModel,
    onVolver: () -> Unit,
    onPagar: () -> Unit,
    carritoViewModel: CarritoViewModel = viewModel()
) {
    val items by carritoViewModel.items.collectAsState(initial = emptyList())
    val subtotal = carritoViewModel.calcularSubtotal(items)
    val despacho = checkoutViewModel.costoDespacho(subtotal)
    val total = subtotal + despacho

    Scaffold(
        topBar = { OurSourTopBar(titulo = "Resumen", onVolver = onVolver) },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    BotonDorado(
                        texto = "Pagar ${formatearPrecio(total)}",
                        icono = Icons.Filled.Payment,
                        habilitado = items.isNotEmpty(),
                        onClick = onPagar
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            SeccionResumen("Productos") {
                items.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text("${item.cantidad} x ${item.nombre}", modifier = Modifier.weight(1f))
                        Text(formatearPrecio(item.precio * item.cantidad))
                    }
                }
            }

            SeccionResumen(if (checkoutViewModel.retiroEnTienda) "Retiro en tienda" else "Despacho a domicilio") {
                Text(checkoutViewModel.nombreReceptor)
                Text("+56 ${checkoutViewModel.telefono}")
                Text(checkoutViewModel.textoEntrega(), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            SeccionResumen("Total") {
                FilaResumen("Subtotal", formatearPrecio(subtotal))
                FilaResumen("Despacho", if (despacho == 0) "Gratis" else formatearPrecio(despacho))
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                FilaResumen("TOTAL", formatearPrecio(total), destacado = true)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SeccionResumen(titulo: String, contenido: @Composable () -> Unit) {
    Spacer(Modifier.height(12.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
            Spacer(Modifier.height(8.dp))
            contenido()
        }
    }
}
