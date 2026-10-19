package cl.oursour.mobile.view

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.util.ReglasDespacho
import cl.oursour.mobile.util.formatearPrecio
import cl.oursour.mobile.view.components.BotonPrincipal
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.viewmodel.CarritoViewModel
import cl.oursour.mobile.viewmodel.CheckoutViewModel

// Dirección y despacho
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DespachoScreen(
    checkoutViewModel: CheckoutViewModel,
    onVolver: () -> Unit,
    onContinuar: () -> Unit,
    carritoViewModel: CarritoViewModel = viewModel()
) {
    val items by carritoViewModel.items.collectAsState(initial = emptyList())
    val subtotal = carritoViewModel.calcularSubtotal(items)
    val costoDomicilio = ReglasDespacho.calcular(subtotal, esRetiroEnTienda = false)

    Scaffold(
        topBar = { OurSourTopBar(titulo = "Despacho", onVolver = onVolver) },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    BotonPrincipal(
                        texto = "Ver resumen",
                        icono = Icons.AutoMirrored.Filled.ArrowForward,
                        onClick = { if (checkoutViewModel.validarDespacho()) onContinuar() }
                    )
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
            Text("¿Cómo quieres recibir tu pedido?", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            OpcionEntrega(
                titulo = "Despacho a domicilio",
                detalle = if (costoDomicilio == 0) "Gratis" else "${formatearPrecio(costoDomicilio)} · Gratis sobre ${formatearPrecio(ReglasDespacho.MONTO_ENVIO_GRATIS)}",
                seleccionada = !checkoutViewModel.retiroEnTienda,
                onClick = { checkoutViewModel.cambiarRetiroEnTienda(false) }
            )
            Spacer(Modifier.height(8.dp))
            OpcionEntrega(
                titulo = "Retiro en tienda",
                detalle = "Gratis · ${checkoutViewModel.direccionTienda}",
                seleccionada = checkoutViewModel.retiroEnTienda,
                onClick = { checkoutViewModel.cambiarRetiroEnTienda(true) }
            )

            Spacer(Modifier.height(24.dp))
            Text("Datos de contacto", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = checkoutViewModel.nombreReceptor,
                onValueChange = { checkoutViewModel.cambiarNombreReceptor(it) },
                label = { Text("Nombre de quien recibe") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = checkoutViewModel.telefono,
                onValueChange = { checkoutViewModel.cambiarTelefono(it) },
                label = { Text("Teléfono (9 dígitos)") },
                prefix = { Text("+56 ") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            if (!checkoutViewModel.retiroEnTienda) {
                Spacer(Modifier.height(24.dp))
                Text("Dirección de despacho", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = checkoutViewModel.direccion,
                    onValueChange = { checkoutViewModel.cambiarDireccion(it) },
                    label = { Text("Calle, número y depto.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Text("Comuna (Región Metropolitana)", style = MaterialTheme.typography.titleMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    checkoutViewModel.comunas.forEach { comuna ->
                        FilterChip(
                            selected = checkoutViewModel.comuna == comuna,
                            onClick = { checkoutViewModel.cambiarComuna(comuna) },
                            label = { Text(comuna) }
                        )
                    }
                }
            }

            checkoutViewModel.errorDespacho?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OpcionEntrega(titulo: String, detalle: String, seleccionada: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().selectable(selected = seleccionada, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionada) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = seleccionada, onClick = onClick)
            Column {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(detalle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
