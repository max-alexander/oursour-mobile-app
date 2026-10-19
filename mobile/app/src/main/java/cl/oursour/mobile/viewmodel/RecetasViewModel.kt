package cl.oursour.mobile.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.oursour.mobile.data.repository.CatalogoRepository
import cl.oursour.mobile.model.RecetaRecomendada
import cl.oursour.mobile.model.TipoReceta

class RecetasViewModel : ViewModel() {

    var tipoSeleccionado by mutableStateOf(TipoReceta.COCTEL)
        private set

    var recomendaciones by mutableStateOf<List<RecetaRecomendada>>(emptyList())
        private set

    fun cargar(ingredientesSeleccionados: Set<Int>) {
        recomendaciones = CatalogoRepository.recomendarRecetas(ingredientesSeleccionados)
    }

    fun cambiarTipo(tipo: TipoReceta) {
        tipoSeleccionado = tipo
    }

    fun recetasDelTipo(): List<RecetaRecomendada> =
        recomendaciones.filter { it.receta.tipo == tipoSeleccionado }
}
