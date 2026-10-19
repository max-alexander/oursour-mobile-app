package cl.oursour.mobile.navigation

// Nombres de todas las rutas (pantallas) de la aplicación
object Routes {
    // Autenticación
    const val BIENVENIDA = "bienvenida"
    const val LOGIN = "login?admin={admin}"
    const val REGISTRO = "registro"

    // Consumidor
    const val HOME = "home"
    const val DESPENSA = "despensa"
    const val MIS_INGREDIENTES = "mis_ingredientes"
    const val RECETAS = "recetas?ingredientes={ingredientes}"
    const val RECETA_DETALLE = "receta/{recetaId}?ingredientes={ingredientes}"
    const val CATALOGO = "catalogo"
    const val PRODUCTO_DETALLE = "producto/{productoId}"
    const val CARRITO = "carrito"
    const val DESPACHO = "despacho"
    const val RESUMEN = "resumen"
    const val PAGO = "pago"
    const val PEDIDO_CONFIRMADO = "pedido_confirmado/{pedidoId}"
    const val PEDIDOS = "pedidos"
    const val ESCANER_QR = "escaner_qr"

    // Administrador
    const val ADMIN_PANEL = "admin"
    const val ADMIN_CATALOGO = "admin/catalogo"
    const val ADMIN_RECETA_FORM = "admin/receta/{recetaId}"
    const val ADMIN_METRICAS = "admin/metricas"

    // Funciones que arman la ruta con sus argumentos
    fun login(admin: Boolean) = "login?admin=$admin"
    fun recetas(ingredientes: List<Int>) = "recetas?ingredientes=${ingredientes.joinToString(",")}"
    fun recetaDetalle(recetaId: Int, ingredientes: Collection<Int> = emptyList()) =
        "receta/$recetaId?ingredientes=${ingredientes.joinToString(",")}"
    fun productoDetalle(productoId: Int) = "producto/$productoId"
    fun pedidoConfirmado(pedidoId: Int) = "pedido_confirmado/$pedidoId"
    fun adminRecetaForm(recetaId: Int) = "admin/receta/$recetaId"

    // "1,2,3" -> {1, 2, 3}
    fun leerIds(texto: String?): Set<Int> =
        texto.orEmpty().split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
}
