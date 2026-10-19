package cl.oursour.mobile.view.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.data.local.entity.ConteoMetrica
import cl.oursour.mobile.view.components.EstadoVacio
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.viewmodel.MetricasViewModel

// Pantalla 8 del wireframe: Tendencias de búsqueda
@Composable
fun MetricasScreen(
    onVolver: () -> Unit,
    metricasViewModel: MetricasViewModel = viewModel()
) {
    val total by metricasViewModel.totalBusquedas.collectAsState(initial = 0)
    val ingredientes by metricasViewModel.ingredientesTop.collectAsState(initial = emptyList())
    val recetas by metricasViewModel.recetasTop.collectAsState(initial = emptyList())
    val combinaciones by metricasViewModel.combinacionesTop.collectAsState(initial = emptyList())
    val porDia by metricasViewModel.busquedasPorDia.collectAsState(initial = emptyList())
    var confirmarReinicio by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            OurSourTopBar(titulo = "Tendencias de búsqueda", onVolver = onVolver, acciones = {
                IconButton(onClick = { confirmarReinicio = true }) {
                    Icon(Icons.Filled.RestartAlt, contentDescription = "Reiniciar métricas")
                }
            })
        }
    ) { padding ->
        if (total == 0 && recetas.isEmpty()) {
            EstadoVacio(
                icono = Icons.Filled.BarChart,
                titulo = "Aún no hay datos",
                mensaje = "Las métricas se generan cuando los usuarios buscan recetas en Mi Despensa y abren recetas.",
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Búsquedas totales")
                    Text("$total", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.secondary)
                }
            }

            SeccionMetrica("Ingredientes más buscados") {
                GraficoBarras(datos = ingredientes)
            }

            SeccionMetrica("Búsquedas de los últimos 7 días") {
                GraficoBarras(datos = porDia)
            }

            SeccionMetrica("Recetas más consultadas") {
                ListaRanking(recetas)
            }

            SeccionMetrica("Combinaciones frecuentes") {
                ListaRanking(combinaciones)
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (confirmarReinicio) {
        AlertDialog(
            onDismissRequest = { confirmarReinicio = false },
            title = { Text("Reiniciar métricas") },
            text = { Text("Se borrarán todos los eventos de búsqueda guardados en el dispositivo.") },
            confirmButton = {
                TextButton(onClick = {
                    metricasViewModel.reiniciarMetricas()
                    confirmarReinicio = false
                }) { Text("Reiniciar") }
            },
            dismissButton = { TextButton(onClick = { confirmarReinicio = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun SeccionMetrica(titulo: String, contenido: @Composable () -> Unit) {
    Spacer(Modifier.height(16.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            contenido()
        }
    }
}

// Gráfico de barras verticales hecho con Box (sin librerías externas)
@Composable
private fun GraficoBarras(datos: List<ConteoMetrica>) {
    if (datos.isEmpty()) {
        Text("Sin datos", color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }
    val maximo = (datos.maxOf { it.total }).coerceAtLeast(1)

    Row(
        modifier = Modifier.fillMaxWidth().height(170.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        datos.forEach { dato ->
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text("${dato.total}", style = MaterialTheme.typography.labelSmall)
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .fillMaxHeight(0.7f * dato.total / maximo + 0.01f)
                        .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                )
                Text(
                    dato.valor,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp, start = 2.dp, end = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun ListaRanking(datos: List<ConteoMetrica>) {
    if (datos.isEmpty()) {
        Text("Sin datos", color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }
    datos.forEachIndexed { indice, dato ->
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text("${indice + 1}.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.width(28.dp))
            Text(dato.valor, modifier = Modifier.weight(1f))
            Text("${dato.total}", fontWeight = FontWeight.Bold)
        }
    }
}
