package cl.oursour.mobile.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.ui.theme.Verde
import cl.oursour.mobile.util.formatearFecha
import cl.oursour.mobile.util.formatearPrecio
import cl.oursour.mobile.view.components.BotonPrincipal
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
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Verde, modifier = Modifier.size(96.dp))
        Spacer(Modifier.height(16.dp))
        Text("¡Pedido confirmado!", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Tu pago fue aprobado y tu pedido quedó registrado.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))

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

        Spacer(Modifier.height(32.dp))
        BotonPrincipal(texto = "Ver mis pedidos", onClick = onVerPedidos)
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onInicio, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp)) {
            Text("Volver al inicio")
        }
    }
}
