package cl.oursour.mobile.model

enum class TipoReceta(val etiqueta: String) {
    COCTEL("Cócteles"),
    MOCKTAIL("Mocktails")
}

// Ingrediente dentro de una receta, con su dosificación
data class RecetaIngrediente(
    val ingredienteId: Int,
    val cantidad: String
)

data class Receta(
    val id: Int,
    val titulo: String,
    val tipo: TipoReceta,
    val descripcion: String,
    val ingredientes: List<RecetaIngrediente>,
    val pasos: List<String>,
    val productoId: Int // Producto Our Sour asociado a la receta
)

// Resultado del motor de sugerencias: la receta, qué tanto coincide y qué falta
data class RecetaRecomendada(
    val receta: Receta,
    val porcentaje: Int,
    val faltantes: List<Ingrediente>
)
