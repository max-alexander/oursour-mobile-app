package cl.oursour.mobile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.oursour.mobile.data.repository.CatalogoRepository
import cl.oursour.mobile.model.Receta
import cl.oursour.mobile.model.RecetaIngrediente
import cl.oursour.mobile.model.TipoReceta

// Formulario para crear (id = 0) o editar una receta desde el panel de administración
class RecetaFormViewModel : ViewModel() {

    var recetaId = 0
        private set

    var titulo by mutableStateOf("")
        private set
    var descripcion by mutableStateOf("")
        private set
    var tipo by mutableStateOf(TipoReceta.COCTEL)
        private set
    var pasos by mutableStateOf("") // Un paso por línea
        private set
    var productoId by mutableStateOf<Int?>(null)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    // ingredienteId -> cantidad. Si el id está en el mapa, el ingrediente está seleccionado.
    val cantidades = mutableStateMapOf<Int, String>()

    private var cargado = false

    fun cargar(id: Int) {
        if (cargado) return
        cargado = true
        val receta = CatalogoRepository.buscarReceta(id) ?: return

        recetaId = receta.id
        titulo = receta.titulo
        descripcion = receta.descripcion
        tipo = receta.tipo
        pasos = receta.pasos.joinToString("\n")
        productoId = receta.productoId
        receta.ingredientes.forEach { cantidades[it.ingredienteId] = it.cantidad }
    }

    fun cambiarTitulo(valor: String) { titulo = valor; error = null }
    fun cambiarDescripcion(valor: String) { descripcion = valor; error = null }
    fun cambiarTipo(valor: TipoReceta) { tipo = valor }
    fun cambiarPasos(valor: String) { pasos = valor; error = null }
    fun cambiarProducto(id: Int) { productoId = id; error = null }

    fun alternarIngrediente(ingredienteId: Int) {
        if (cantidades.containsKey(ingredienteId)) {
            cantidades.remove(ingredienteId)
        } else {
            cantidades[ingredienteId] = ""
        }
        error = null
    }

    fun cambiarCantidad(ingredienteId: Int, valor: String) {
        cantidades[ingredienteId] = valor
        error = null
    }

    fun guardar(onExito: () -> Unit) {
        val listaPasos = pasos.lines().map { it.trim() }.filter { it.isNotEmpty() }
        error = when {
            titulo.isBlank() -> "El título es obligatorio"
            cantidades.size < 2 -> "Selecciona al menos 2 ingredientes"
            cantidades.values.any { it.isBlank() } -> "Indica la cantidad de cada ingrediente"
            listaPasos.isEmpty() -> "Agrega al menos un paso de preparación"
            productoId == null -> "Selecciona el producto Our Sour asociado"
            else -> null
        }
        if (error != null) return

        CatalogoRepository.guardarReceta(
            Receta(
                id = recetaId,
                titulo = titulo.trim(),
                tipo = tipo,
                descripcion = descripcion.trim(),
                ingredientes = cantidades.map { RecetaIngrediente(it.key, it.value.trim()) },
                pasos = listaPasos,
                productoId = productoId!!
            )
        )
        onExito()
    }
}
