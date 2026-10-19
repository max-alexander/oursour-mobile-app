package cl.oursour.mobile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.oursour.mobile.data.repository.CatalogoRepository
import cl.oursour.mobile.model.CategoriaIngrediente
import cl.oursour.mobile.model.Ingrediente
import cl.oursour.mobile.model.Producto

class AdminCatalogoViewModel : ViewModel() {

    // 0 = Recetas, 1 = Ingredientes, 2 = Productos
    var pestana by mutableIntStateOf(0)
        private set

    // Mensaje que se muestra en un Snackbar
    var mensaje by mutableStateOf<String?>(null)
        private set

    fun cambiarPestana(indice: Int) {
        pestana = indice
    }

    fun mensajeMostrado() {
        mensaje = null
    }

    // ---------- Recetas ----------
    fun eliminarReceta(id: Int) {
        CatalogoRepository.eliminarReceta(id)
        mensaje = "Receta eliminada"
    }

    // ---------- Ingredientes ----------
    // Devuelve un error de validación o null si se guardó correctamente
    fun guardarIngrediente(id: Int, nombre: String, categoria: CategoriaIngrediente): String? {
        if (nombre.isBlank()) return "El nombre es obligatorio"
        val repetido = CatalogoRepository.ingredientes.any {
            it.nombre.equals(nombre.trim(), ignoreCase = true) && it.id != id
        }
        if (repetido) return "Ya existe un ingrediente con ese nombre"

        CatalogoRepository.guardarIngrediente(Ingrediente(id, nombre.trim(), categoria))
        mensaje = if (id == 0) "Ingrediente creado" else "Ingrediente actualizado"
        return null
    }

    fun eliminarIngrediente(id: Int) {
        mensaje = CatalogoRepository.eliminarIngrediente(id) ?: "Ingrediente eliminado"
    }

    // ---------- Productos ----------
    fun guardarProducto(id: Int, nombre: String, descripcion: String, precioTexto: String, stockTexto: String): String? {
        val precio = precioTexto.toIntOrNull()
        val stock = stockTexto.toIntOrNull()
        if (nombre.isBlank()) return "El nombre es obligatorio"
        if (descripcion.isBlank()) return "La descripción es obligatoria"
        if (precio == null || precio <= 0) return "El precio debe ser un número mayor a 0"
        if (stock == null || stock < 0) return "El stock debe ser un número igual o mayor a 0"

        val existente = CatalogoRepository.buscarProducto(id)
        val ingredienteId = if (existente != null) {
            // Se mantiene sincronizado el nombre del ingrediente asociado
            CatalogoRepository.guardarIngrediente(Ingrediente(existente.ingredienteId, nombre.trim(), CategoriaIngrediente.OUR_SOUR))
            existente.ingredienteId
        } else {
            // Todo producto nuevo también aparece como ingrediente "Producto Our Sour" en la despensa
            CatalogoRepository.guardarIngrediente(Ingrediente(0, nombre.trim(), CategoriaIngrediente.OUR_SOUR))
            CatalogoRepository.ingredientes.last().id
        }

        CatalogoRepository.guardarProducto(Producto(id, nombre.trim(), descripcion.trim(), precio, stock, ingredienteId))
        mensaje = if (id == 0) "Producto creado" else "Producto actualizado"
        return null
    }

    fun eliminarProducto(id: Int) {
        mensaje = CatalogoRepository.eliminarProducto(id) ?: "Producto eliminado"
    }
}
