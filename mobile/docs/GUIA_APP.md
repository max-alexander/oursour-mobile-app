# Our Sour Mobile – Guía de la app (Prueba 2)

App Android en **Kotlin + Jetpack Compose + Material 3**, arquitectura **MVVM**.
En esta etapa **no se conecta al backend**: el catálogo usa datos ficticios en memoria y los datos del usuario se guardan en **Room**.

## Paquetes (`cl.oursour.mobile`)

| Paquete | Responsabilidad |
|---|---|
| `MainActivity.kt` | Punto de entrada. Aplica el tema y carga `AppNavigation`. |
| `model/` | Clases de datos del dominio: `Ingrediente`, `Receta`, `Producto`, `Usuario` y sus enums. |
| `data/local/` | Base de datos **Room**: `AppDatabase` (singleton), `entity/` (tablas) y `dao/` (consultas). |
| `data/repository/` | `CatalogoRepository`: catálogo ficticio + **motor de sugerencia de recetas** + CRUD del admin. `SesionUsuario`: usuario conectado. |
| `data/remote/` | Cliente Retrofit. **No se usa todavía** (Unidad 3). |
| `navigation/` | `Routes` (nombres de rutas) y `AppNavigation` (NavHost con todas las pantallas). |
| `view/` | Pantallas Compose del consumidor. `view/admin/` pantallas del administrador. `view/components/` componentes reutilizables. |
| `viewmodel/` | Un ViewModel por pantalla/flujo. Guardan el estado (`mutableStateOf`) y la lógica. |
| `ui/theme/` | Colores, tipografía y tema Material 3 con la paleta Our Sour. |
| `util/` | Formato de precios/fechas y reglas de despacho. |

## Dependencias

| Dependencia | Para qué |
|---|---|
| Compose BOM + Material 3 | Interfaz gráfica |
| `material-icons-extended` | Iconos |
| `navigation-compose` | Navegación entre pantallas |
| `lifecycle-viewmodel-compose` | `viewModel()` dentro de Compose |
| `room-runtime`, `room-ktx`, `room-compiler` (KSP) | Base de datos local SQLite |
| `zxing-android-embedded` | Leer QR con la cámara y generar imágenes QR |
| Retrofit / OkHttp | Conexión al backend (Unidad 3, sin uso aún) |

## Base de datos local (Room) – `oursour.db`

| Tabla | Uso |
|---|---|
| `usuarios` | Cuentas creadas en "Crear cuenta" (login local) |
| `ingredientes_frecuentes` | Ingredientes marcados con ⭐ en Mi Despensa |
| `carrito` | Productos del carro (persiste al cerrar la app) |
| `pedidos` | Pedidos confirmados después del pago sandbox |
| `eventos_busqueda` | Búsquedas, ingredientes, combinaciones y recetas vistas → **métricas del admin** |

Los DAO devuelven `Flow`, y las pantallas los leen con `collectAsState()`, así que la UI se actualiza sola cuando cambia la tabla.

## Lectura de QR

`EscanerQrScreen` usa `rememberLauncherForActivityResult(ScanContract())` de ZXing. Formato de los códigos:

- `OURSOUR:PRODUCTO:<id>` → abre el detalle del producto
- `OURSOUR:RECETA:<id>` → abre la receta
- `OURSOUR:INGREDIENTE:<id>` → lo guarda en ingredientes frecuentes (Room)

La interpretación está en `EscanerQrViewModel.procesarCodigo()`. Los QR de prueba están en `docs/qr/`. Además, el detalle de producto y el de receta generan su propio QR (`QrImagen` en `components/`).

## Cuentas de prueba

- **Administrador:** `admin@oursour.cl` / `admin123` (botón "Acceso administrador")
- **Consumidor:** crear una cuenta en "Regístrate", o entrar como invitado
- **Tarjeta sandbox:** `4242 4242 4242 4242` aprueba · cualquier tarjeta terminada en `0000` es rechazada

## Guion sugerido para el video (≤ 7 min)

1. Bienvenida → Crear cuenta (mostrar validaciones) → Home.
2. Mi Despensa: buscar, marcar ingredientes, guardar ⭐ frecuentes.
3. Ver recetas compatibles (% de coincidencia, pestañas Cócteles/Mocktails).
4. Detalle de receta: ✓/✗ ingredientes, pasos, "Añadir al carro".
5. Carro: cambiar cantidades, eliminar, envío gratis sobre $50.000.
6. Despacho → Resumen → Pago (primero tarjeta rechazada `...0000`, luego aprobada) → Pedido confirmado → Mis pedidos.
7. Cerrar y reabrir la app: el carro, los frecuentes y los pedidos siguen ahí (**Room**).
8. Escanear QR de un producto, de una receta y de un ingrediente.
9. Cerrar sesión → Acceso administrador → Gestionar catálogo (crear/editar/eliminar) → Métricas.

## Preguntas que pueden hacer en la demo en vivo (dónde está cada cosa)

| Funcionalidad | Archivo |
|---|---|
| Motor de recomendación | `data/repository/CatalogoRepository.kt` → `recomendarRecetas()` |
| Rutas y navegación | `navigation/Routes.kt`, `navigation/AppNavigation.kt` |
| Validación de login / registro | `viewmodel/LoginViewModel.kt`, `viewmodel/RegistroViewModel.kt` |
| Costo de despacho | `util/Formato.kt` → `ReglasDespacho` |
| Pago simulado y creación de pedido | `viewmodel/CheckoutViewModel.kt` → `pagar()` |
| Lógica del carro | `viewmodel/CarritoViewModel.kt` |
| Métricas (consultas GROUP BY) | `data/local/dao/EventoBusquedaDao.kt`, `viewmodel/MetricasViewModel.kt` |
| Colores de la app | `ui/theme/Color.kt` |
