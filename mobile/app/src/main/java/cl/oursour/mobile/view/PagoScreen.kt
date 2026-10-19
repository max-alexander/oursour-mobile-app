package cl.oursour.mobile.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cl.oursour.mobile.view.components.BotonDorado
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.viewmodel.CheckoutViewModel

// Pago simulado (sandbox). No se usan ni guardan datos reales de tarjetas.
@Composable
fun PagoScreen(
    checkoutViewModel: CheckoutViewModel,
    onVolver: () -> Unit,
    onPagoExitoso: (Int) -> Unit
) {
    val procesando = checkoutViewModel.procesando

    Scaffold(
        topBar = { OurSourTopBar(titulo = "Pago", onVolver = if (procesando) null else onVolver) },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (procesando) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Procesando pago...")
                        }
                    } else {
                        BotonDorado(
                            texto = "Confirmar pago",
                            icono = Icons.Filled.Lock,
                            onClick = { checkoutViewModel.pagar(onPagoExitoso) }
                        )
                    }
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
            // Vista previa de la tarjeta
            Card(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(8.dp))
                        Text("Our Sour Pay · Sandbox")
                    }
                    Text(
                        checkoutViewModel.numeroTarjeta.padEnd(16, '•').chunked(4).joinToString(" "),
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = FontFamily.Monospace
                    )
                    Row {
                        Text(checkoutViewModel.titular.ifBlank { "NOMBRE TITULAR" }.uppercase(), modifier = Modifier.weight(1f))
                        Text(checkoutViewModel.vencimiento.ifBlank { "MM/AA" })
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = checkoutViewModel.numeroTarjeta,
                onValueChange = { checkoutViewModel.cambiarNumeroTarjeta(it) },
                label = { Text("Número de tarjeta") },
                singleLine = true,
                enabled = !procesando,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = checkoutViewModel.titular,
                onValueChange = { checkoutViewModel.cambiarTitular(it) },
                label = { Text("Nombre del titular") },
                singleLine = true,
                enabled = !procesando,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row {
                OutlinedTextField(
                    value = checkoutViewModel.vencimiento,
                    onValueChange = { checkoutViewModel.cambiarVencimiento(it) },
                    label = { Text("Vencimiento (MM/AA)") },
                    singleLine = true,
                    enabled = !procesando,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = checkoutViewModel.cvv,
                    onValueChange = { checkoutViewModel.cambiarCvv(it) },
                    label = { Text("CVV") },
                    singleLine = true,
                    enabled = !procesando,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.weight(1f)
                )
            }

            checkoutViewModel.errorPago?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(20.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Modo sandbox", style = MaterialTheme.typography.titleMedium)
                    Text("• 4242 4242 4242 4242 → pago aprobado", style = MaterialTheme.typography.bodySmall)
                    Text("• Cualquier tarjeta terminada en 0000 → pago rechazado", style = MaterialTheme.typography.bodySmall)
                    Text("No se realizan cobros reales.", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (checkoutViewModel.pagoRechazado) {
        AlertDialog(
            onDismissRequest = { checkoutViewModel.cerrarRechazo() },
            icon = { Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Pago rechazado") },
            text = { Text("La pasarela sandbox rechazó la tarjeta. Revisa los datos o intenta con otra tarjeta.") },
            confirmButton = {
                TextButton(onClick = { checkoutViewModel.cerrarRechazo() }) { Text("Reintentar") }
            }
        )
    }
}
