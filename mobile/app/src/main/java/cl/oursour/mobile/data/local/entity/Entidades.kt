package cl.oursour.mobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Cada @Entity es una tabla de la base de datos SQLite local

// Usuarios registrados en el dispositivo (mientras no exista conexión al backend)
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val email: String,
    val nombre: String,
    val password: String
)

// "Marquita" de sesión iniciada: si existe esta fila, la app abre directo en el home.
// Siempre hay como máximo una fila (id = 1); al cerrar sesión se borra.
@Entity(tableName = "sesion")
data class SesionEntity(
    @PrimaryKey val id: Int = 1,
    val email: String,
    val nombre: String,
    val rol: String // CONSUMIDOR o ADMINISTRADOR
)

// Ingredientes que el usuario guarda como frecuentes para seleccionarlos rápido
@Entity(tableName = "ingredientes_frecuentes")
data class IngredienteFrecuenteEntity(
    @PrimaryKey val ingredienteId: Int,
    val nombre: String,
    val categoria: String
)

// Productos agregados al carro de compras
@Entity(tableName = "carrito")
data class CarritoItemEntity(
    @PrimaryKey val productoId: Int,
    val nombre: String,
    val precio: Int,
    val cantidad: Int
)

// Pedidos confirmados después del pago simulado
@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioEmail: String,
    val fecha: Long,
    val detalle: String, // Ej: "2 x Our Sour Limón;1 x Our Sour Mango"
    val tipoEntrega: String,
    val direccion: String,
    val subtotal: Int,
    val despacho: Int,
    val total: Int,
    val estado: String
)

// Registro de búsquedas y consultas, usado en las métricas del administrador
@Entity(tableName = "eventos_busqueda")
data class EventoBusquedaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipo: String, // BUSQUEDA, INGREDIENTE, COMBINACION o RECETA
    val valor: String,
    val fecha: Long
)

// Valores posibles de la columna "tipo" de eventos_busqueda
object TipoEvento {
    const val BUSQUEDA = "BUSQUEDA"
    const val INGREDIENTE = "INGREDIENTE"
    const val COMBINACION = "COMBINACION"
    const val RECETA = "RECETA"
}

// Resultado de las consultas agrupadas (no es una tabla)
data class ConteoMetrica(
    val valor: String,
    val total: Int
)
