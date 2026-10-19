package cl.oursour.mobile.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.oursour.mobile.data.local.AppDatabase
import cl.oursour.mobile.data.local.entity.PedidoEntity
import cl.oursour.mobile.data.repository.CatalogoRepository
import cl.oursour.mobile.data.repository.SesionUsuario
import cl.oursour.mobile.util.ReglasDespacho
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ViewModel compartido por las pantallas Despacho -> Resumen -> Pago,
// así los datos ingresados en una pantalla siguen disponibles en la siguiente.
class CheckoutViewModel(application: Application) : AndroidViewModel(application) {

    private val carritoDao = AppDatabase.obtener(application).carritoDao()
    private val pedidoDao = AppDatabase.obtener(application).pedidoDao()

    val direccionTienda = "Av. Italia 1234, Providencia"
    val comunas = listOf("Santiago", "Providencia", "Las Condes", "Ñuñoa", "Vitacura", "La Reina", "Maipú", "La Florida")

    // ---------- DESPACHO ----------
    var retiroEnTienda by mutableStateOf(false)
        private set
    var nombreReceptor by mutableStateOf("")
        private set
    var direccion by mutableStateOf("")
        private set
    var comuna by mutableStateOf("")
        private set
    var telefono by mutableStateOf("")
        private set
    var errorDespacho by mutableStateOf<String?>(null)
        private set

    fun cambiarRetiroEnTienda(valor: Boolean) {
        retiroEnTienda = valor
        errorDespacho = null
    }

    fun cambiarNombreReceptor(valor: String) {
        nombreReceptor = valor
        errorDespacho = null
    }

    fun cambiarDireccion(valor: String) {
        direccion = valor
        errorDespacho = null
    }

    fun cambiarComuna(valor: String) {
        comuna = valor
        errorDespacho = null
    }

    fun cambiarTelefono(valor: String) {
        telefono = valor.filter { it.isDigit() }.take(9)
        errorDespacho = null
    }

    fun validarDespacho(): Boolean {
        errorDespacho = when {
            nombreReceptor.isBlank() -> "Ingresa el nombre de quien recibe"
            telefono.length != 9 -> "El teléfono debe tener 9 dígitos"
            !retiroEnTienda && direccion.isBlank() -> "Ingresa la dirección de despacho"
            !retiroEnTienda && comuna.isBlank() -> "Selecciona una comuna"
            else -> null
        }
        return errorDespacho == null
    }

    fun costoDespacho(subtotal: Int): Int = ReglasDespacho.calcular(subtotal, retiroEnTienda)

    fun textoEntrega(): String =
        if (retiroEnTienda) "Retiro en tienda: $direccionTienda" else "$direccion, $comuna"

    // ---------- PAGO SANDBOX ----------
    var numeroTarjeta by mutableStateOf("")
        private set
    var titular by mutableStateOf("")
        private set
    var vencimiento by mutableStateOf("")
        private set
    var cvv by mutableStateOf("")
        private set
    var errorPago by mutableStateOf<String?>(null)
        private set
    var procesando by mutableStateOf(false)
        private set
    var pagoRechazado by mutableStateOf(false)
        private set

    fun cambiarNumeroTarjeta(valor: String) {
        numeroTarjeta = valor.filter { it.isDigit() }.take(16)
        errorPago = null
    }

    fun cambiarTitular(valor: String) {
        titular = valor
        errorPago = null
    }

    fun cambiarVencimiento(valor: String) {
        vencimiento = valor.take(5)
        errorPago = null
    }

    fun cambiarCvv(valor: String) {
        cvv = valor.filter { it.isDigit() }.take(3)
        errorPago = null
    }

    fun cerrarRechazo() {
        pagoRechazado = false
    }

    private fun validarPago(): Boolean {
        errorPago = when {
            numeroTarjeta.length != 16 -> "El número de tarjeta debe tener 16 dígitos"
            titular.isBlank() -> "Ingresa el nombre del titular"
            !Regex("(0[1-9]|1[0-2])/\\d{2}").matches(vencimiento) -> "Vencimiento inválido (formato MM/AA)"
            cvv.length != 3 -> "El CVV debe tener 3 dígitos"
            else -> null
        }
        return errorPago == null
    }

    // Simula la respuesta de una pasarela de pago sandbox.
    // Las tarjetas terminadas en 0000 son rechazadas para poder probar el caso de error.
    fun pagar(onPedidoCreado: (Int) -> Unit) {
        if (!validarPago()) return
        procesando = true

        viewModelScope.launch {
            delay(2000)

            if (numeroTarjeta.endsWith("0000")) {
                procesando = false
                pagoRechazado = true
                return@launch
            }

            val items = carritoDao.obtenerLista()
            val subtotal = items.sumOf { it.precio * it.cantidad }
            val despacho = costoDespacho(subtotal)

            val pedidoId = pedidoDao.insertar(
                PedidoEntity(
                    usuarioEmail = SesionUsuario.emailActual,
                    fecha = System.currentTimeMillis(),
                    detalle = items.joinToString(";") { "${it.cantidad} x ${it.nombre}" },
                    tipoEntrega = if (retiroEnTienda) "Retiro en tienda" else "Despacho a domicilio",
                    direccion = textoEntrega(),
                    subtotal = subtotal,
                    despacho = despacho,
                    total = subtotal + despacho,
                    estado = "Solicitado"
                )
            )

            items.forEach { CatalogoRepository.descontarStock(it.productoId, it.cantidad) }
            carritoDao.vaciar()
            limpiarPago()
            procesando = false
            onPedidoCreado(pedidoId.toInt())
        }
    }

    private fun limpiarPago() {
        numeroTarjeta = ""
        titular = ""
        vencimiento = ""
        cvv = ""
    }
}
