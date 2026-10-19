package cl.oursour.mobile.model

// Categorías en las que se agrupan los ingredientes en "Mi Despensa"
enum class CategoriaIngrediente(val etiqueta: String) {
    OUR_SOUR("Productos Our Sour"),
    DESTILADO("Destilados"),
    LICOR("Licores"),
    FRUTA("Frutas"),
    JUGO("Jugos"),
    MIXER("Mixers"),
    OTRO("Otros")
}

data class Ingrediente(
    val id: Int,
    val nombre: String,
    val categoria: CategoriaIngrediente
)
