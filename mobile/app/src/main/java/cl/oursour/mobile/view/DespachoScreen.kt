package cl.oursour.mobile.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                .padding(16.dp)
        ) {
            TituloSeccion("¿Cómo quieres recibir tu pedido?", primero = true)

            OpcionEntrega(
                titulo = "Despacho a domicilio",
                detalle = if (costoDomicilio == 0) "Gratis" else "${formatearPrecio(costoDomicilio)} · Gratis sobre ${formatearPrecio(ReglasDespacho.MONTO_ENVIO_GRATIS)}",
                seleccionada = !checkoutViewModel.retiroEnTienda,
                onClick = { checkoutViewModel.cambiarRetiroEnTienda(false) }
            )
            OpcionEntrega(
                titulo = "Retiro en tienda",
                detalle = "Gratis · ${checkoutViewModel.direccionTienda}",
                seleccionada = checkoutViewModel.retiroEnTienda,
                onClick = { checkoutViewModel.cambiarRetiroEnTienda(true) },
                modifier = Modifier.padding(top = 8.dp)
            )

            TituloSeccion("Datos de contacto")
            OutlinedTextField(
                value = checkoutViewModel.nombreReceptor,
                onValueChange = { checkoutViewModel.cambiarNombreReceptor(it) },
                label = { Text("Nombre de quien recibe") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
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
                TituloSeccion("Dirección de despacho")
                OutlinedTextField(
                    value = checkoutViewModel.direccion,
                    onValueChange = { checkoutViewModel.cambiarDireccion(it) },
                    label = { Text("Calle, número y depto.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Comuna (Región Metropolitana)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 12.dp)
                )
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
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

// Título de cada bloque del formulario
@Composable
private fun TituloSeccion(texto: String, primero: Boolean = false) {
    Text(
        text = texto,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = if (primero) 0.dp else 24.dp, bottom = 8.dp)
    )
}

@Composable
private fun OpcionEntrega(
    titulo: String,
    detalle: String,
    seleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().selectable(selected = seleccionada, onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionada) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = seleccionada, onClick = onClick)
            Column {
                Text(text = titulo, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(text = detalle, fontSize = 12.sp)
            }
        }
    }
}
