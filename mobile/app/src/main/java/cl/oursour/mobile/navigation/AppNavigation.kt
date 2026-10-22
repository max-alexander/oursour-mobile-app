package cl.oursour.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.oursour.mobile.data.repository.SesionUsuario
import cl.oursour.mobile.view.BienvenidaScreen
import cl.oursour.mobile.view.CarritoScreen
import cl.oursour.mobile.view.CatalogoScreen
import cl.oursour.mobile.view.DespachoScreen
import cl.oursour.mobile.view.DespensaScreen
import cl.oursour.mobile.view.EscanerQrScreen
import cl.oursour.mobile.view.HomeScreen
import cl.oursour.mobile.view.LoginScreen
import cl.oursour.mobile.view.MisIngredientesScreen
import cl.oursour.mobile.view.PagoScreen
import cl.oursour.mobile.view.PedidoConfirmadoScreen
import cl.oursour.mobile.view.PedidosScreen
import cl.oursour.mobile.view.ProductoDetalleScreen
import cl.oursour.mobile.view.RecetaDetalleScreen
import cl.oursour.mobile.view.RecetasScreen
import cl.oursour.mobile.view.RegistroScreen
import cl.oursour.mobile.view.ResumenScreen
import cl.oursour.mobile.view.admin.AdminPanelScreen
import cl.oursour.mobile.view.admin.GestionCatalogoScreen
import cl.oursour.mobile.view.admin.MetricasScreen
import cl.oursour.mobile.view.admin.RecetaFormScreen
import cl.oursour.mobile.viewmodel.CheckoutViewModel
import cl.oursour.mobile.viewmodel.SesionViewModel

// Define todas las pantallas y cómo se navega entre ellas
@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {

    // Se crea aquí para que Despacho, Resumen y Pago compartan la MISMA instancia
    val checkoutViewModel: CheckoutViewModel = viewModel()

    // Revisa si quedó una sesión guardada en la base de datos local
    val sesionViewModel: SesionViewModel = viewModel()

    // Mientras se consulta la base de datos no se muestra nada (es casi instantáneo)
    val rutaInicial = sesionViewModel.rutaInicial ?: return

    // Borra la sesión guardada, vuelve a la bienvenida y borra todo el historial
    fun cerrarSesion() {
        sesionViewModel.cerrarSesion()
        navController.navigate(Routes.BIENVENIDA) {
            popUpTo(0) { inclusive = true }
        }
    }

    // Entra al home (o panel admin) borrando las pantallas de login del historial
    fun entrarA(ruta: String) {
        navController.navigate(ruta) {
            popUpTo(Routes.BIENVENIDA) { inclusive = true }
        }
    }

    NavHost(navController = navController, startDestination = rutaInicial) {

        // ---------------- AUTENTICACIÓN ----------------
        composable(Routes.BIENVENIDA) {
            BienvenidaScreen(
                onIniciarSesion = { navController.navigate(Routes.login(admin = false)) },
                onInvitado = {
                    SesionUsuario.iniciarComoInvitado()
                    entrarA(Routes.HOME)
                },
                onAccesoAdmin = { navController.navigate(Routes.login(admin = true)) }
            )
        }

        composable(
            route = Routes.LOGIN,
            arguments = listOf(navArgument("admin") { type = NavType.BoolType; defaultValue = false })
        ) { backStackEntry ->
            LoginScreen(
                modoAdmin = backStackEntry.arguments?.getBoolean("admin") ?: false,
                onVolver = { navController.popBackStack() },
                onLoginConsumidor = { entrarA(Routes.HOME) },
                onLoginAdmin = { entrarA(Routes.ADMIN_PANEL) },
                onIrARegistro = { navController.navigate(Routes.REGISTRO) }
            )
        }

        composable(Routes.REGISTRO) {
            RegistroScreen(
                onVolver = { navController.popBackStack() },
                onRegistroExitoso = { entrarA(Routes.HOME) }
            )
        }

        // ---------------- CONSUMIDOR ----------------
        composable(Routes.HOME) {
            HomeScreen(
                onBuscarRecetas = { navController.navigate(Routes.DESPENSA) },
                onMisIngredientes = { navController.navigate(Routes.MIS_INGREDIENTES) },
                onProductos = { navController.navigate(Routes.CATALOGO) },
                onMisPedidos = { navController.navigate(Routes.PEDIDOS) },
                onTodasLasRecetas = { navController.navigate(Routes.recetas(emptyList())) },
                onEscanearQr = { navController.navigate(Routes.ESCANER_QR) },
                onCarrito = { navController.navigate(Routes.CARRITO) },
                onCerrarSesion = { cerrarSesion() }
            )
        }

        composable(Routes.DESPENSA) {
            DespensaScreen(
                onVolver = { navController.popBackStack() },
                onCarrito = { navController.navigate(Routes.CARRITO) },
                onVerRecetas = { ids -> navController.navigate(Routes.recetas(ids)) }
            )
        }

        composable(Routes.MIS_INGREDIENTES) {
            MisIngredientesScreen(
                onVolver = { navController.popBackStack() },
                onIrADespensa = { navController.navigate(Routes.DESPENSA) },
                onBuscarRecetas = { ids -> navController.navigate(Routes.recetas(ids)) }
            )
        }

        composable(
            route = Routes.RECETAS,
            arguments = listOf(navArgument("ingredientes") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            val ids = Routes.leerIds(backStackEntry.arguments?.getString("ingredientes"))
            RecetasScreen(
                ingredientesSeleccionados = ids,
                onVolver = { navController.popBackStack() },
                onCarrito = { navController.navigate(Routes.CARRITO) },
                onVerReceta = { recetaId -> navController.navigate(Routes.recetaDetalle(recetaId, ids)) }
            )
        }

        composable(
            route = Routes.RECETA_DETALLE,
            arguments = listOf(
                navArgument("recetaId") { type = NavType.IntType },
                navArgument("ingredientes") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            RecetaDetalleScreen(
                recetaId = backStackEntry.arguments?.getInt("recetaId") ?: 0,
                ingredientesSeleccionados = Routes.leerIds(backStackEntry.arguments?.getString("ingredientes")),
                onVolver = { navController.popBackStack() },
                onCarrito = { navController.navigate(Routes.CARRITO) },
                onVerProducto = { productoId -> navController.navigate(Routes.productoDetalle(productoId)) }
            )
        }

        composable(Routes.CATALOGO) {
            CatalogoScreen(
                onVolver = { navController.popBackStack() },
                onCarrito = { navController.navigate(Routes.CARRITO) },
                onEscanearQr = { navController.navigate(Routes.ESCANER_QR) },
                onVerProducto = { productoId -> navController.navigate(Routes.productoDetalle(productoId)) }
            )
        }

        composable(
            route = Routes.PRODUCTO_DETALLE,
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { backStackEntry ->
            ProductoDetalleScreen(
                productoId = backStackEntry.arguments?.getInt("productoId") ?: 0,
                onVolver = { navController.popBackStack() },
                onCarrito = { navController.navigate(Routes.CARRITO) },
                onVerReceta = { recetaId -> navController.navigate(Routes.recetaDetalle(recetaId)) }
            )
        }

        composable(Routes.ESCANER_QR) {
            EscanerQrScreen(
                onVolver = { navController.popBackStack() },
                onAbrirProducto = { productoId -> navController.navigate(Routes.productoDetalle(productoId)) },
                onAbrirReceta = { recetaId -> navController.navigate(Routes.recetaDetalle(recetaId)) }
            )
        }

        // ---------------- COMPRA ----------------
        composable(Routes.CARRITO) {
            CarritoScreen(
                onVolver = { navController.popBackStack() },
                onIrACatalogo = { navController.navigate(Routes.CATALOGO) },
                onContinuarCompra = { navController.navigate(Routes.DESPACHO) }
            )
        }

        composable(Routes.DESPACHO) {
            DespachoScreen(
                checkoutViewModel = checkoutViewModel,
                onVolver = { navController.popBackStack() },
                onContinuar = { navController.navigate(Routes.RESUMEN) }
            )
        }

        composable(Routes.RESUMEN) {
            ResumenScreen(
                checkoutViewModel = checkoutViewModel,
                onVolver = { navController.popBackStack() },
                onPagar = { navController.navigate(Routes.PAGO) }
            )
        }

        composable(Routes.PAGO) {
            PagoScreen(
                checkoutViewModel = checkoutViewModel,
                onVolver = { navController.popBackStack() },
                onPagoExitoso = { pedidoId ->
                    // Se eliminan carro/despacho/resumen/pago del historial
                    navController.navigate(Routes.pedidoConfirmado(pedidoId)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(
            route = Routes.PEDIDO_CONFIRMADO,
            arguments = listOf(navArgument("pedidoId") { type = NavType.IntType })
        ) { backStackEntry ->
            PedidoConfirmadoScreen(
                pedidoId = backStackEntry.arguments?.getInt("pedidoId") ?: 0,
                onVerPedidos = {
                    navController.navigate(Routes.PEDIDOS) { popUpTo(Routes.HOME) }
                },
                onInicio = { navController.popBackStack(Routes.HOME, inclusive = false) }
            )
        }

        composable(Routes.PEDIDOS) {
            PedidosScreen(
                onVolver = { navController.popBackStack() },
                onIrACatalogo = { navController.navigate(Routes.CATALOGO) }
            )
        }

        // ---------------- ADMINISTRADOR ----------------
        composable(Routes.ADMIN_PANEL) {
            AdminPanelScreen(
                onGestionCatalogo = { navController.navigate(Routes.ADMIN_CATALOGO) },
                onMetricas = { navController.navigate(Routes.ADMIN_METRICAS) },
                onCerrarSesion = { cerrarSesion() }
            )
        }

        composable(Routes.ADMIN_CATALOGO) {
            GestionCatalogoScreen(
                onVolver = { navController.popBackStack() },
                onNuevaReceta = { navController.navigate(Routes.adminRecetaForm(0)) },
                onEditarReceta = { recetaId -> navController.navigate(Routes.adminRecetaForm(recetaId)) }
            )
        }

        composable(
            route = Routes.ADMIN_RECETA_FORM,
            arguments = listOf(navArgument("recetaId") { type = NavType.IntType })
        ) { backStackEntry ->
            RecetaFormScreen(
                recetaId = backStackEntry.arguments?.getInt("recetaId") ?: 0,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN_METRICAS) {
            MetricasScreen(onVolver = { navController.popBackStack() })
        }
    }
}
