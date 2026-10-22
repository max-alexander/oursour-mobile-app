package cl.oursour.mobile.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.ui.theme.Verde
import cl.oursour.mobile.util.formatearFecha
import cl.oursour.mobile.util.formatearPrecio
import cl.oursour.mobile.view.components.FilaResumen
import cl.oursour.mobile.viewmodel.PedidosViewModel

@Composable
fun PedidoConfirmadoScreen(
    pedidoId: Int,
    onVerPedidos: () -> Unit,
    onInicio: () -> Unit,
    pedidosViewModel: PedidosViewModel = viewModel()
) {
    LaunchedEffect(pedidoId) { pedidosViewModel.cargarPedido(pedidoId) }
    val pedido = pedidosViewModel.pedido

    // El botón "atrás" lleva al inicio (no se vuelve a la pantalla de pago)
    BackHandler { onInicio() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = Verde,
            modifier = Modifier.padding(bottom = 16.dp).size(96.dp)
        )
        Text(
            text = "¡Pedido confirmado!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tu pago fue aprobado y tu pedido quedó registrado.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (pedido != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    FilaResumen("Pedido", "#${pedido.id}")
                    FilaResumen("Fecha", formatearFecha(pedido.fecha))
                    FilaResumen("Entrega", pedido.tipoEntrega)
                    FilaResumen("Estado", pedido.estado)
                    FilaResumen("Total", formatearPrecio(pedido.total), destacado = true)
                }
            }
        }

        Button(
            onClick = onVerPedidos,
            modifier = Modifier.padding(top = 32.dp).fillMaxWidth()
        ) {
            Text(text = "Ver mis pedidos")
        }
        OutlinedButton(
            onClick = onInicio,
            modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
        ) {
            Text(text = "Volver al inicio")
        }
    }
}
