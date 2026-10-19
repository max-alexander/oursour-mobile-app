package cl.oursour.mobile.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.oursour.mobile.data.repository.SesionUsuario
import cl.oursour.mobile.view.components.CarritoBoton
import cl.oursour.mobile.view.components.OurSourTopBar

@Composable
fun HomeScreen(
    onBuscarRecetas: () -> Unit,
    onMisIngredientes: () -> Unit,
    onProductos: () -> Unit,
    onMisPedidos: () -> Unit,
    onTodasLasRecetas: () -> Unit,
    onEscanearQr: () -> Unit,
    onCarrito: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val nombre = SesionUsuario.usuario?.nombre ?: "Invitado"

    Scaffold(
        topBar = {
            OurSourTopBar(titulo = "Our Sour", acciones = {
                CarritoBoton(onClick = onCarrito)
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
                .padding(horizontal = 20.dp)
        ) {
            Text("Hola, $nombre 👋", style = MaterialTheme.typography.headlineSmall)
            Text(
                "¿Qué vamos a preparar hoy?",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))

            // Tarjeta principal: el flujo más importante de la app
            Card(
                onClick = onBuscarRecetas,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Buscar recetas", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Marca los ingredientes que tienes en casa y descubre qué preparar.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        Icons.Filled.Kitchen,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OpcionHome("Mis ingredientes", Icons.Filled.Star, onMisIngredientes, Modifier.weight(1f))
                OpcionHome("Productos", Icons.Filled.Storefront, onProductos, Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OpcionHome("Mis pedidos", Icons.AutoMirrored.Filled.ReceiptLong, onMisPedidos, Modifier.weight(1f))
                OpcionHome("Todas las recetas", Icons.AutoMirrored.Filled.MenuBook, onTodasLasRecetas, Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))

            Card(
                onClick = onEscanearQr,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.QrCodeScanner, contentDescription = null, modifier = Modifier.size(36.dp))
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Escanear código QR", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Escanea el QR de un producto Our Sour o de una receta",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (SesionUsuario.esInvitado) {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Estás navegando como invitado. Crea una cuenta para guardar tus pedidos con tu correo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OpcionHome(texto: String, icono: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(32.dp))
            Text(texto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}
