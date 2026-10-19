package cl.oursour.mobile.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.oursour.mobile.data.local.AppDatabase
import cl.oursour.mobile.data.local.entity.EventoBusquedaEntity
import cl.oursour.mobile.data.local.entity.TipoEvento
import cl.oursour.mobile.data.repository.CatalogoRepository
import cl.oursour.mobile.model.Producto
import cl.oursour.mobile.model.Receta
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RecetaDetalleViewModel(application: Application) : AndroidViewModel(application) {

    private val frecuenteDao = AppDatabase.obtener(application).ingredienteFrecuenteDao()
    private val eventoDao = AppDatabase.obtener(application).eventoBusquedaDao()

    var receta by mutableStateOf<Receta?>(null)
        private set

    // Ingredientes que el usuario tiene disponibles
    var disponibles by mutableStateOf<Set<Int>>(emptySet())
        private set

    private var cargada = false

    fun cargar(recetaId: Int, seleccionados: Set<Int>) {
        if (cargada) return
        cargada = true
        receta = CatalogoRepository.buscarReceta(recetaId)

        viewModelScope.launch {
            // Si no viene una selección (ej: desde un QR), se usan los ingredientes frecuentes
            disponibles = seleccionados.ifEmpty {
                frecuenteDao.obtenerTodos().first().map { it.ingredienteId }.toSet()
            }
            receta?.let {
                eventoDao.insertar(
                    listOf(EventoBusquedaEntity(tipo = TipoEvento.RECETA, valor = it.titulo, fecha = System.currentTimeMillis()))
                )
            }
        }
    }

    fun productoAsociado(): Producto? = receta?.let { CatalogoRepository.buscarProducto(it.productoId) }

    fun tieneIngrediente(ingredienteId: Int): Boolean = ingredienteId in disponibles

    fun faltaProductoOurSour(): Boolean {
        val producto = productoAsociado() ?: return false
        return !tieneIngrediente(producto.ingredienteId)
    }
}
