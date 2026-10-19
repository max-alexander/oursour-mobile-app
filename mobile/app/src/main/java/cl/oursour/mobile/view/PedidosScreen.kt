package cl.oursour.mobile.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import cl.oursour.mobile.data.local.entity.PedidoEntity
import cl.oursour.mobile.util.formatearFecha
import cl.oursour.mobile.util.formatearPrecio
import cl.oursour.mobile.view.components.EstadoVacio
import cl.oursour.mobile.view.components.FilaResumen
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.viewmodel.PedidosViewModel

// Historial de pedidos guardados en la base de datos local
@Composable
fun PedidosScreen(
    onVolver: () -> Unit,
    onIrACatalogo: () -> Unit,
    pedidosViewModel: PedidosViewModel = viewModel()
) {
    val pedidos by pedidosViewModel.pedidos.collectAsState(initial = emptyList())

    Scaffold(
        topBar = { OurSourTopBar(titulo = "Mis pedidos", onVolver = onVolver) }
    ) { padding ->
        if (pedidos.isEmpty()) {
            EstadoVacio(
                icono = Icons.AutoMirrored.Filled.ReceiptLong,
                titulo = "Aún no tienes pedidos",
                mensaje = "Cuando completes una compra, aparecerá aquí.",
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
            items(pedidos, key = { it.id }) { pedido ->
                TarjetaPedido(pedido)
            }
        }
    }
}

@Composable
private fun TarjetaPedido(pedido: PedidoEntity) {
    var expandido by remember { mutableStateOf(false) }

    Card(
        onClick = { expandido = !expandido },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pedido #${pedido.id}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(formatearFecha(pedido.fecha), style = MaterialTheme.typography.bodySmall)
                }
                AssistChip(onClick = { expandido = !expandido }, label = { Text(pedido.estado) })
                Icon(if (expandido) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
            }
            Text(formatearPrecio(pedido.total), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.tertiary)

            AnimatedVisibility(visible = expandido) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    pedido.detalle.split(";").forEach { Text("• $it") }
                    Spacer(Modifier.height(8.dp))
                    Text(pedido.tipoEntrega, fontWeight = FontWeight.SemiBold)
                    Text(pedido.direccion, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    FilaResumen("Subtotal", formatearPrecio(pedido.subtotal))
                    FilaResumen("Despacho", if (pedido.despacho == 0) "Gratis" else formatearPrecio(pedido.despacho))
                    FilaResumen("Total", formatearPrecio(pedido.total))
                }
            }
        }
    }
}
