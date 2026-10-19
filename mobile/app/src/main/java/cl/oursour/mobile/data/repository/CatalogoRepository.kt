package cl.oursour.mobile.data.repository

import androidx.compose.runtime.mutableStateListOf
import cl.oursour.mobile.model.CategoriaIngrediente
import cl.oursour.mobile.model.Ingrediente
import cl.oursour.mobile.model.Producto
import cl.oursour.mobile.model.Receta
import cl.oursour.mobile.model.RecetaIngrediente
import cl.oursour.mobile.model.RecetaRecomendada
import cl.oursour.mobile.model.TipoReceta

// Catálogo maestro con datos ficticios.
// En la Unidad 3 estos datos vendrán del backend (Retrofit); por ahora viven en memoria.
// Se usan listas "mutableStateListOf" para que las pantallas se actualicen
// cuando el administrador crea, edita o elimina elementos.
object CatalogoRepository {

    val ingredientes = mutableStateListOf(
        // Productos Our Sour
        Ingrediente(1, "Our Sour Limón", CategoriaIngrediente.OUR_SOUR),
        Ingrediente(2, "Our Sour Maracuyá", CategoriaIngrediente.OUR_SOUR),
        Ingrediente(3, "Our Sour Mango", CategoriaIngrediente.OUR_SOUR),
        Ingrediente(4, "Our Sour Frutilla", CategoriaIngrediente.OUR_SOUR),
        Ingrediente(5, "Our Sour Mojito", CategoriaIngrediente.OUR_SOUR),
        Ingrediente(6, "Our Sour Jengibre", CategoriaIngrediente.OUR_SOUR),
        Ingrediente(7, "Our Sour Pomelo", CategoriaIngrediente.OUR_SOUR),
        // Destilados
        Ingrediente(10, "Pisco", CategoriaIngrediente.DESTILADO),
        Ingrediente(11, "Gin", CategoriaIngrediente.DESTILADO),
        Ingrediente(12, "Vodka", CategoriaIngrediente.DESTILADO),
        Ingrediente(13, "Ron blanco", CategoriaIngrediente.DESTILADO),
        Ingrediente(14, "Tequila", CategoriaIngrediente.DESTILADO),
        Ingrediente(15, "Whisky", CategoriaIngrediente.DESTILADO),
        // Licores
        Ingrediente(20, "Triple Sec", CategoriaIngrediente.LICOR),
        Ingrediente(21, "Licor de café", CategoriaIngrediente.LICOR),
        Ingrediente(22, "Vermut rojo", CategoriaIngrediente.LICOR),
        // Frutas
        Ingrediente(30, "Limón", CategoriaIngrediente.FRUTA),
        Ingrediente(31, "Naranja", CategoriaIngrediente.FRUTA),
        Ingrediente(32, "Menta", CategoriaIngrediente.FRUTA),
        Ingrediente(33, "Piña", CategoriaIngrediente.FRUTA),
        Ingrediente(34, "Frutilla", CategoriaIngrediente.FRUTA),
        // Jugos
        Ingrediente(40, "Jugo de limón", CategoriaIngrediente.JUGO),
        Ingrediente(41, "Jugo de naranja", CategoriaIngrediente.JUGO),
        Ingrediente(42, "Jugo de piña", CategoriaIngrediente.JUGO),
        Ingrediente(43, "Jugo de arándano", CategoriaIngrediente.JUGO),
        // Mixers
        Ingrediente(50, "Agua tónica", CategoriaIngrediente.MIXER),
        Ingrediente(51, "Soda", CategoriaIngrediente.MIXER),
        Ingrediente(52, "Ginger ale", CategoriaIngrediente.MIXER),
        Ingrediente(53, "Bebida cola", CategoriaIngrediente.MIXER),
        // Otros
        Ingrediente(60, "Hielo", CategoriaIngrediente.OTRO),
        Ingrediente(61, "Azúcar", CategoriaIngrediente.OTRO),
        Ingrediente(62, "Clara de huevo", CategoriaIngrediente.OTRO),
        Ingrediente(63, "Amargo de angostura", CategoriaIngrediente.OTRO),
        Ingrediente(64, "Sal", CategoriaIngrediente.OTRO)
    )

    val productos = mutableStateListOf(
        Producto(1, "Our Sour Limón", "Base en polvo sabor limón de pica, lista para sours y margaritas. Rinde 10 tragos.", 7990, 25, 1),
        Producto(2, "Our Sour Maracuyá", "Base en polvo de maracuyá natural, ideal para sours tropicales y limonadas.", 8490, 18, 2),
        Producto(3, "Our Sour Mango", "Base en polvo de mango, perfecta para daiquiris y mocktails frutales.", 8490, 12, 3),
        Producto(4, "Our Sour Frutilla", "Base en polvo de frutilla, para daiquiris, fizz y tragos dulces.", 8490, 20, 4),
        Producto(5, "Our Sour Mojito", "Mezcla de menta y limón lista para mojitos con o sin alcohol.", 7990, 30, 5),
        Producto(6, "Our Sour Jengibre", "Base de jengibre y limón para mules y bebidas refrescantes.", 8990, 15, 6),
        Producto(7, "Our Sour Pomelo", "Base de pomelo rosado para palomas y tónicas cítricas.", 8990, 10, 7)
    )

    val recetas = mutableStateListOf(
        Receta(
            1, "Pisco Sour Tradicional", TipoReceta.COCTEL,
            "El clásico chileno, cremoso y equilibrado.",
            listOf(
                RecetaIngrediente(10, "3 oz"),
                RecetaIngrediente(1, "1 sobre"),
                RecetaIngrediente(62, "1 unidad"),
                RecetaIngrediente(63, "3 gotas"),
                RecetaIngrediente(60, "1 taza")
            ),
            listOf(
                "Agregar el pisco, Our Sour Limón y la clara a la coctelera.",
                "Añadir hielo y agitar fuerte por 15 segundos.",
                "Colar en una copa y terminar con gotas de amargo de angostura."
            ),
            productoId = 1
        ),
        Receta(
            2, "Mojito Clásico", TipoReceta.COCTEL,
            "Refrescante, mentolado y cítrico.",
            listOf(
                RecetaIngrediente(13, "2 oz"),
                RecetaIngrediente(5, "1 sobre"),
                RecetaIngrediente(32, "8 hojas"),
                RecetaIngrediente(51, "100 ml"),
                RecetaIngrediente(60, "1 taza")
            ),
            listOf(
                "Machacar suavemente la menta en el vaso.",
                "Agregar Our Sour Mojito, el ron y el hielo.",
                "Completar con soda y revolver."
            ),
            productoId = 5
        ),
        Receta(
            3, "Gin Tonic Cítrico", TipoReceta.COCTEL,
            "Un gin tonic con un toque de limón Our Sour.",
            listOf(
                RecetaIngrediente(11, "2 oz"),
                RecetaIngrediente(50, "150 ml"),
                RecetaIngrediente(1, "1/2 sobre"),
                RecetaIngrediente(30, "1 rodaja"),
                RecetaIngrediente(60, "1 taza")
            ),
            listOf(
                "Llenar una copa balón con hielo.",
                "Agregar el gin y Our Sour Limón.",
                "Completar con agua tónica y decorar con limón."
            ),
            productoId = 1
        ),
        Receta(
            4, "Maracuyá Sour", TipoReceta.COCTEL,
            "La versión tropical del pisco sour.",
            listOf(
                RecetaIngrediente(10, "3 oz"),
                RecetaIngrediente(2, "1 sobre"),
                RecetaIngrediente(62, "1 unidad"),
                RecetaIngrediente(60, "1 taza")
            ),
            listOf(
                "Agregar todos los ingredientes a la coctelera.",
                "Agitar fuerte por 15 segundos.",
                "Colar y servir en copa fría."
            ),
            productoId = 2
        ),
        Receta(
            5, "Margarita", TipoReceta.COCTEL,
            "Tequila, naranja y limón con borde de sal.",
            listOf(
                RecetaIngrediente(14, "2 oz"),
                RecetaIngrediente(20, "1 oz"),
                RecetaIngrediente(1, "1 sobre"),
                RecetaIngrediente(64, "Para el borde"),
                RecetaIngrediente(60, "1 taza")
            ),
            listOf(
                "Escarchar el borde de la copa con sal.",
                "Agitar tequila, triple sec, Our Sour Limón y hielo.",
                "Colar en la copa escarchada."
            ),
            productoId = 1
        ),
        Receta(
            6, "Daiquiri de Mango", TipoReceta.COCTEL,
            "Frozen, dulce y tropical.",
            listOf(
                RecetaIngrediente(13, "2 oz"),
                RecetaIngrediente(3, "1 sobre"),
                RecetaIngrediente(60, "2 tazas")
            ),
            listOf(
                "Licuar el ron, Our Sour Mango y el hielo.",
                "Servir en copa y decorar con una rodaja de fruta."
            ),
            productoId = 3
        ),
        Receta(
            7, "Moscow Mule", TipoReceta.COCTEL,
            "Vodka y jengibre, servido bien frío.",
            listOf(
                RecetaIngrediente(12, "2 oz"),
                RecetaIngrediente(6, "1 sobre"),
                RecetaIngrediente(51, "120 ml"),
                RecetaIngrediente(30, "1/2 unidad"),
                RecetaIngrediente(60, "1 taza")
            ),
            listOf(
                "Llenar un vaso de cobre con hielo.",
                "Agregar el vodka y Our Sour Jengibre.",
                "Completar con soda y decorar con limón."
            ),
            productoId = 6
        ),
        Receta(
            8, "Whisky Sour", TipoReceta.COCTEL,
            "Intenso, cítrico y sedoso.",
            listOf(
                RecetaIngrediente(15, "2 oz"),
                RecetaIngrediente(1, "1 sobre"),
                RecetaIngrediente(62, "1 unidad"),
                RecetaIngrediente(63, "2 gotas"),
                RecetaIngrediente(60, "1 taza")
            ),
            listOf(
                "Agitar en seco el whisky, Our Sour Limón y la clara.",
                "Agregar hielo y agitar nuevamente.",
                "Colar en vaso corto con hielo y añadir angostura."
            ),
            productoId = 1
        ),
        Receta(
            9, "Paloma", TipoReceta.COCTEL,
            "El trago más popular de México.",
            listOf(
                RecetaIngrediente(14, "2 oz"),
                RecetaIngrediente(7, "1 sobre"),
                RecetaIngrediente(51, "120 ml"),
                RecetaIngrediente(64, "Una pizca"),
                RecetaIngrediente(60, "1 taza")
            ),
            listOf(
                "Escarchar un vaso alto con sal.",
                "Agregar hielo, tequila y Our Sour Pomelo.",
                "Completar con soda y revolver."
            ),
            productoId = 7
        ),
        Receta(
            10, "Virgin Mojito", TipoReceta.MOCKTAIL,
            "Todo el frescor del mojito, sin alcohol.",
            listOf(
                RecetaIngrediente(5, "1 sobre"),
                RecetaIngrediente(32, "8 hojas"),
                RecetaIngrediente(51, "200 ml"),
                RecetaIngrediente(60, "1 taza")
            ),
            listOf(
                "Machacar la menta en el vaso.",
                "Agregar Our Sour Mojito y hielo.",
                "Completar con soda y revolver."
            ),
            productoId = 5
        ),
        Receta(
            11, "Limonada de Maracuyá", TipoReceta.MOCKTAIL,
            "Ácida, dulce y muy refrescante.",
            listOf(
                RecetaIngrediente(2, "1 sobre"),
                RecetaIngrediente(51, "200 ml"),
                RecetaIngrediente(32, "4 hojas"),
                RecetaIngrediente(60, "1 taza")
            ),
            listOf(
                "Disolver Our Sour Maracuyá en la soda.",
                "Servir sobre hielo y decorar con menta."
            ),
            productoId = 2
        ),
        Receta(
            12, "Mango Tonic", TipoReceta.MOCKTAIL,
            "Burbujeante y frutal.",
            listOf(
                RecetaIngrediente(3, "1 sobre"),
                RecetaIngrediente(50, "200 ml"),
                RecetaIngrediente(60, "1 taza")
            ),
            listOf(
                "Agregar Our Sour Mango a un vaso con hielo.",
                "Completar con agua tónica y revolver suavemente."
            ),
            productoId = 3
        ),
        Receta(
            13, "Ginger Lemonade", TipoReceta.MOCKTAIL,
            "Mule sin alcohol con jengibre y limón.",
            listOf(
                RecetaIngrediente(6, "1 sobre"),
                RecetaIngrediente(52, "200 ml"),
                RecetaIngrediente(30, "1/2 unidad"),
                RecetaIngrediente(60, "1 taza")
            ),
            listOf(
                "Agregar Our Sour Jengibre y hielo al vaso.",
                "Completar con ginger ale.",
                "Exprimir el limón y revolver."
            ),
            productoId = 6
        ),
        Receta(
            14, "Frutilla Fizz", TipoReceta.MOCKTAIL,
            "Dulce, rosado y con burbujas.",
            listOf(
                RecetaIngrediente(4, "1 sobre"),
                RecetaIngrediente(34, "3 unidades"),
                RecetaIngrediente(51, "200 ml"),
                RecetaIngrediente(60, "1 taza")
            ),
            listOf(
                "Machacar las frutillas en el vaso.",
                "Agregar Our Sour Frutilla y hielo.",
                "Completar con soda."
            ),
            productoId = 4
        ),
        Receta(
            15, "Pomelo Tonic", TipoReceta.MOCKTAIL,
            "Cítrico y ligeramente amargo.",
            listOf(
                RecetaIngrediente(7, "1 sobre"),
                RecetaIngrediente(50, "200 ml"),
                RecetaIngrediente(60, "1 taza")
            ),
            listOf(
                "Agregar Our Sour Pomelo a una copa con hielo.",
                "Completar con agua tónica."
            ),
            productoId = 7
        )
    )

    // ---------- Consultas ----------

    fun buscarIngrediente(id: Int): Ingrediente? = ingredientes.find { it.id == id }

    fun buscarProducto(id: Int): Producto? = productos.find { it.id == id }

    fun buscarReceta(id: Int): Receta? = recetas.find { it.id == id }

    fun recetasConProducto(productoId: Int): List<Receta> =
        recetas.filter { it.productoId == productoId }

    // ---------- Motor de sugerencia de recetas ----------
    // Compara los ingredientes seleccionados con los de cada receta y calcula
    // el porcentaje de coincidencia. Solo devuelve recetas con al menos un ingrediente en común.
    fun recomendarRecetas(seleccionados: Set<Int>): List<RecetaRecomendada> {
        val resultado = mutableListOf<RecetaRecomendada>()

        for (receta in recetas) {
            val idsReceta = receta.ingredientes.map { it.ingredienteId }
            val coincidencias = idsReceta.count { it in seleccionados }

            if (seleccionados.isEmpty() || coincidencias > 0) {
                val porcentaje = if (idsReceta.isEmpty()) 0 else coincidencias * 100 / idsReceta.size
                val faltantes = idsReceta
                    .filter { it !in seleccionados }
                    .mapNotNull { buscarIngrediente(it) }
                resultado.add(RecetaRecomendada(receta, porcentaje, faltantes))
            }
        }

        // Primero las recetas con mayor coincidencia
        return resultado.sortedByDescending { it.porcentaje }
    }

    // ---------- Gestión del catálogo (administrador) ----------

    fun guardarIngrediente(ingrediente: Ingrediente) {
        val indice = ingredientes.indexOfFirst { it.id == ingrediente.id }
        if (indice >= 0) {
            ingredientes[indice] = ingrediente
        } else {
            ingredientes.add(ingrediente.copy(id = (ingredientes.maxOfOrNull { it.id } ?: 0) + 1))
        }
    }

    // Devuelve un mensaje de error si el ingrediente no se puede eliminar
    fun eliminarIngrediente(id: Int): String? {
        val usos = recetas.count { receta -> receta.ingredientes.any { it.ingredienteId == id } }
        if (usos > 0) return "No se puede eliminar: se usa en $usos receta(s)"
        if (productos.any { it.ingredienteId == id }) return "No se puede eliminar: está asociado a un producto"
        ingredientes.removeAll { it.id == id }
        return null
    }

    fun guardarProducto(producto: Producto) {
        val indice = productos.indexOfFirst { it.id == producto.id }
        if (indice >= 0) {
            productos[indice] = producto
        } else {
            productos.add(producto.copy(id = (productos.maxOfOrNull { it.id } ?: 0) + 1))
        }
    }

    fun eliminarProducto(id: Int): String? {
        val usos = recetas.count { it.productoId == id }
        if (usos > 0) return "No se puede eliminar: está asociado a $usos receta(s)"
        productos.removeAll { it.id == id }
        return null
    }

    fun guardarReceta(receta: Receta) {
        val indice = recetas.indexOfFirst { it.id == receta.id }
        if (indice >= 0) {
            recetas[indice] = receta
        } else {
            recetas.add(receta.copy(id = (recetas.maxOfOrNull { it.id } ?: 0) + 1))
        }
    }

    fun eliminarReceta(id: Int) {
        recetas.removeAll { it.id == id }
    }

    // Se descuenta el stock simulado cuando se confirma un pedido
    fun descontarStock(productoId: Int, cantidad: Int) {
        val producto = buscarProducto(productoId) ?: return
        guardarProducto(producto.copy(stock = (producto.stock - cantidad).coerceAtLeast(0)))
    }
}
