package cl.oursour.mobile.view.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.R
import cl.oursour.mobile.model.TipoReceta
import cl.oursour.mobile.viewmodel.CarritoViewModel
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

// Componentes reutilizables para mantener un diseño consistente en toda la app

// Barra superior con botón "volver" opcional y acciones a la derecha
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OurSourTopBar(
    titulo: String,
    onVolver: (() -> Unit)? = null,
    acciones: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = { Text(titulo, fontWeight = FontWeight.Bold) },
        navigationIcon = {
            if (onVolver != null) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        },
        actions = { acciones() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

// Icono del carro con la cantidad de productos (leída desde Room)
@Composable
fun CarritoBoton(
    onClick: () -> Unit,
    carritoViewModel: CarritoViewModel = viewModel()
) {
    val unidades by carritoViewModel.unidades.collectAsState(initial = 0)
    IconButton(onClick = onClick) {
        BadgedBox(badge = {
            if (unidades > 0) Badge { Text("$unidades") }
        }) {
            Icon(Icons.Filled.ShoppingCart, contentDescription = "Carro de compras")
        }
    }
}

// Botón principal de ancho completo
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    icono: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null)
            Spacer(Modifier.width(8.dp))
        }
        Text(texto, fontWeight = FontWeight.Bold)
    }
}

// Botón dorado para las acciones de compra
@Composable
fun BotonDorado(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    icono: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        if (icono != null) {
            Icon(icono, contentDescription = null)
            Spacer(Modifier.width(8.dp))
        }
        Text(texto, fontWeight = FontWeight.Bold)
    }
}

// Mensaje para listas vacías
@Composable
fun EstadoVacio(
    icono: ImageVector,
    titulo: String,
    mensaje: String,
    modifier: Modifier = Modifier,
    accion: @Composable () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icono,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(Modifier.height(16.dp))
        Text(titulo, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        accion()
    }
}

// Círculo con el icono del tipo de receta
@Composable
fun IconoReceta(tipo: TipoReceta, tamano: Dp = 52.dp) {
    Box(
        modifier = Modifier
            .size(tamano)
            .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (tipo == TipoReceta.COCTEL) Icons.Filled.LocalBar else Icons.Filled.LocalDrink,
            contentDescription = tipo.etiqueta,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(tamano / 2)
        )
    }
}

// Selector "-  2  +" para cantidades
@Composable
fun SelectorCantidad(
    cantidad: Int,
    onCambiar: (Int) -> Unit,
    maximo: Int = Int.MAX_VALUE
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedIconButton(onClick = { onCambiar(cantidad - 1) }, modifier = Modifier.size(34.dp)) {
            Icon(Icons.Filled.Remove, contentDescription = "Restar", modifier = Modifier.size(18.dp))
        }
        Text(
            "$cantidad",
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedIconButton(
            onClick = { onCambiar(cantidad + 1) },
            enabled = cantidad < maximo,
            modifier = Modifier.size(34.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Sumar", modifier = Modifier.size(18.dp))
        }
    }
}

// Fila "etiqueta ........ valor" usada en resúmenes de compra
@Composable
fun FilaResumen(etiqueta: String, valor: String, destacado: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            etiqueta,
            style = if (destacado) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge
        )
        Text(
            valor,
            style = if (destacado) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
            fontWeight = if (destacado) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// Genera y muestra un código QR a partir de un texto (usa ZXing)
@Composable
fun QrImagen(contenido: String, modifier: Modifier = Modifier, tamano: Dp = 180.dp) {
    val bitmap = remember(contenido) {
        BarcodeEncoder().encodeBitmap(contenido, BarcodeFormat.QR_CODE, 512, 512)
    }
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Código QR: $contenido",
        modifier = modifier.size(tamano)
    )
}

// Imagen de cada producto, guardada en res/drawable.
// Los productos nuevos que cree el administrador usan la imagen genérica.
@DrawableRes
fun imagenProducto(productoId: Int): Int = when (productoId) {
    1 -> R.drawable.producto_limon
    2 -> R.drawable.producto_maracuya
    3 -> R.drawable.producto_mango
    4 -> R.drawable.producto_frutilla
    5 -> R.drawable.producto_mojito
    6 -> R.drawable.producto_jengibre
    7 -> R.drawable.producto_pomelo
    else -> R.drawable.producto_generico
}

@Composable
fun ImagenProducto(productoId: Int, modifier: Modifier = Modifier, esquinas: Dp = 12.dp) {
    Image(
        painter = painterResource(id = imagenProducto(productoId)),
        contentDescription = null,
        // Crop: llena el espacio recortando los bordes sin deformar la imagen
        contentScale = ContentScale.Crop,
        modifier = modifier.clip(RoundedCornerShape(esquinas))
    )
}
