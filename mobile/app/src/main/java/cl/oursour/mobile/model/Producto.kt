package cl.oursour.mobile.model

data class Producto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val stock: Int,
    val ingredienteId: Int // Ingrediente de la despensa que representa este producto
)
