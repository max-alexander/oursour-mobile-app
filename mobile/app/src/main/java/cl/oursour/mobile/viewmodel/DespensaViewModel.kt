package cl.oursour.mobile.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.oursour.mobile.data.local.AppDatabase
import cl.oursour.mobile.data.local.entity.EventoBusquedaEntity
import cl.oursour.mobile.data.local.entity.IngredienteFrecuenteEntity
import cl.oursour.mobile.data.local.entity.TipoEvento
import cl.oursour.mobile.data.repository.CatalogoRepository
import cl.oursour.mobile.model.CategoriaIngrediente
import cl.oursour.mobile.model.Ingrediente
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// Se usa en "Mi Despensa" y en "Mis ingredientes frecuentes"
class DespensaViewModel(application: Application) : AndroidViewModel(application) {

    private val frecuenteDao = AppDatabase.obtener(application).ingredienteFrecuenteDao()
    private val eventoDao = AppDatabase.obtener(application).eventoBusquedaDao()

    // Ingredientes frecuentes guardados en Room
    val frecuentes: Flow<List<IngredienteFrecuenteEntity>> = frecuenteDao.obtenerTodos()

    var busqueda by mutableStateOf("")
        private set

    // Ids de los ingredientes marcados por el usuario
    val seleccionados = mutableStateListOf<Int>()

    fun cambiarBusqueda(texto: String) {
        busqueda = texto
    }

    // Agrupa los ingredientes por categoría, aplicando el filtro de búsqueda
    fun ingredientesPorCategoria(): Map<CategoriaIngrediente, List<Ingrediente>> {
        val filtrados = CatalogoRepository.ingredientes.filter {
            it.nombre.contains(busqueda.trim(), ignoreCase = true)
        }
        return CategoriaIngrediente.entries
            .associateWith { categoria -> filtrados.filter { it.categoria == categoria } }
            .filterValues { it.isNotEmpty() }
    }

    fun alternarSeleccion(ingredienteId: Int) {
        if (seleccionados.contains(ingredienteId)) {
            seleccionados.remove(ingredienteId)
        } else {
            seleccionados.add(ingredienteId)
        }
    }

    fun seleccionarFrecuentes(lista: List<IngredienteFrecuenteEntity>) {
        lista.forEach {
            if (!seleccionados.contains(it.ingredienteId)) seleccionados.add(it.ingredienteId)
        }
    }

    fun limpiarSeleccion() {
        seleccionados.clear()
    }

    fun alternarFrecuente(ingrediente: Ingrediente, esFrecuente: Boolean) {
        viewModelScope.launch {
            if (esFrecuente) {
                frecuenteDao.eliminar(ingrediente.id)
            } else {
                frecuenteDao.insertar(
                    IngredienteFrecuenteEntity(ingrediente.id, ingrediente.nombre, ingrediente.categoria.etiqueta)
                )
            }
        }
    }

    fun eliminarFrecuente(ingredienteId: Int) {
        viewModelScope.launch { frecuenteDao.eliminar(ingredienteId) }
    }

    fun eliminarTodosLosFrecuentes() {
        viewModelScope.launch { frecuenteDao.eliminarTodos() }
    }

    // Guarda la búsqueda en Room para las métricas del administrador
    fun registrarBusqueda(ids: List<Int>) {
        val nombres = ids.mapNotNull { CatalogoRepository.buscarIngrediente(it)?.nombre }.sorted()
        val ahora = System.currentTimeMillis()

        val eventos = mutableListOf(EventoBusquedaEntity(tipo = TipoEvento.BUSQUEDA, valor = "${ids.size}", fecha = ahora))
        nombres.forEach { eventos.add(EventoBusquedaEntity(tipo = TipoEvento.INGREDIENTE, valor = it, fecha = ahora)) }
        if (nombres.size >= 2) {
            eventos.add(EventoBusquedaEntity(tipo = TipoEvento.COMBINACION, valor = nombres.joinToString(" + "), fecha = ahora))
        }

        viewModelScope.launch { eventoDao.insertar(eventos) }
    }
}
