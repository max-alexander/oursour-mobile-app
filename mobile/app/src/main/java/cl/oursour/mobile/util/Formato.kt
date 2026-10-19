package cl.oursour.mobile.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Funciones de ayuda para mostrar datos en pantalla

// 12990 -> "$12.990"
fun formatearPrecio(valor: Int): String {
    return "$" + "%,d".format(valor).replace(',', '.')
}

// Milisegundos -> "26/10/2026 18:30"
fun formatearFecha(fecha: Long): String {
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formato.format(Date(fecha))
}

// Reglas de despacho definidas por el equipo
object ReglasDespacho {
    const val COSTO_DESPACHO = 3990
    const val MONTO_ENVIO_GRATIS = 50000

    fun calcular(subtotal: Int, esRetiroEnTienda: Boolean): Int {
        return if (esRetiroEnTienda || subtotal >= MONTO_ENVIO_GRATIS || subtotal == 0) 0 else COSTO_DESPACHO
    }
}
