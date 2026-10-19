package cl.oursour.mobile.view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.ui.theme.Verde
import cl.oursour.mobile.view.components.BotonPrincipal
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.viewmodel.EscanerQrViewModel
import cl.oursour.mobile.viewmodel.ResultadoQr
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

// Lectura de códigos QR con la cámara (librería ZXing)
@Composable
fun EscanerQrScreen(
    onVolver: () -> Unit,
    onAbrirProducto: (Int) -> Unit,
    onAbrirReceta: (Int) -> Unit,
    escanerViewModel: EscanerQrViewModel = viewModel()
) {
    var codigoManual by remember { mutableStateOf("") }
    var cancelado by remember { mutableStateOf(false) }

    // Decide qué hacer según el contenido del QR
    fun manejarCodigo(contenido: String) {
        when (val resultado = escanerViewModel.procesarCodigo(contenido)) {
            is ResultadoQr.AbrirProducto -> onAbrirProducto(resultado.productoId)
            is ResultadoQr.AbrirReceta -> onAbrirReceta(resultado.recetaId)
            else -> Unit // Se muestra el resultado en esta misma pantalla
        }
    }

    // Abre la cámara (CaptureActivity de ZXing) y recibe el texto leído.
    // La librería solicita el permiso de cámara automáticamente.
    val lanzadorEscaner = rememberLauncherForActivityResult(ScanContract()) { resultado ->
        if (resultado.contents == null) {
            cancelado = true
        } else {
            cancelado = false
            manejarCodigo(resultado.contents)
        }
    }

    Scaffold(
        topBar = { OurSourTopBar(titulo = "Escanear QR", onVolver = onVolver) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.QrCodeScanner,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(110.dp)
                )
            }
            Spacer(Modifier.height(20.dp))
            Text("Escanea un código Our Sour", style = MaterialTheme.typography.titleLarge)
            Text(
                "• QR del envase de un producto → abre el producto\n" +
                    "• QR de una receta → abre la receta\n" +
                    "• QR de un ingrediente → lo guarda en tus frecuentes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            BotonPrincipal(
                texto = "Abrir cámara",
                icono = Icons.Filled.CameraAlt,
                onClick = {
                    val opciones = ScanOptions()
                        .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                        .setPrompt("Apunta la cámara al código QR")
                        .setBeepEnabled(true)
                        .setOrientationLocked(false)
                    lanzadorEscaner.launch(opciones)
                }
            )

            if (cancelado) {
                Text(
                    "Escaneo cancelado",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Resultado del último escaneo que no navegó a otra pantalla
            when (val resultado = escanerViewModel.ultimoResultado) {
                is ResultadoQr.IngredienteGuardado -> TarjetaResultado(
                    exito = true,
                    texto = "\"${resultado.nombre}\" se guardó en tus ingredientes frecuentes"
                )
                is ResultadoQr.NoReconocido -> TarjetaResultado(
                    exito = false,
                    texto = "Código no reconocido: ${resultado.contenido}"
                )
                else -> Unit
            }

            // Alternativa para probar en el emulador sin cámara
            Spacer(Modifier.height(32.dp))
            Text("¿Sin cámara? Ingresa el código", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = codigoManual,
                onValueChange = { codigoManual = it.uppercase() },
                placeholder = { Text("OURSOUR:PRODUCTO:1") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { if (codigoManual.isNotBlank()) manejarCodigo(codigoManual) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Procesar código")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TarjetaResultado(exito: Boolean, texto: String) {
    Spacer(Modifier.height(16.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (exito) Icons.Filled.CheckCircle else Icons.Filled.ErrorOutline,
                contentDescription = null,
                tint = if (exito) Verde else MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.width(12.dp))
            Text(texto)
        }
    }
}
