package cl.oursour.mobile.view.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.oursour.mobile.data.repository.CatalogoRepository
import cl.oursour.mobile.data.repository.SesionUsuario
import cl.oursour.mobile.view.components.OurSourTopBar

// Pantalla 6 del wireframe: Panel administrador
@Composable
fun AdminPanelScreen(
    onGestionCatalogo: () -> Unit,
    onMetricas: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Scaffold(
        topBar = {
            OurSourTopBar(titulo = "Panel administrador", acciones = {
                IconButton(onClick = onCerrarSesion) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                }
            })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text("Hola, ${SesionUsuario.usuario?.nombre ?: "Administrador"}", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Catálogo actual: ${CatalogoRepository.recetas.size} recetas · " +
                    "${CatalogoRepository.ingredientes.size} ingredientes · ${CatalogoRepository.productos.size} productos",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))

            OpcionPanel(
                icono = Icons.Filled.Inventory2,
                titulo = "Gestión de catálogo",
                detalle = "Crear, editar y eliminar recetas, ingredientes y productos",
                onClick = onGestionCatalogo
            )
            Spacer(Modifier.height(16.dp))
            OpcionPanel(
                icono = Icons.Filled.BarChart,
                titulo = "Métricas de búsqueda",
                detalle = "Ingredientes más buscados, recetas más consultadas y tendencias",
                onClick = onMetricas
            )
        }
    }
}

@Composable
private fun OpcionPanel(icono: ImageVector, titulo: String, detalle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text(titulo, style = MaterialTheme.typography.titleLarge)
            Text(
                detalle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
