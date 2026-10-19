package cl.oursour.mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.oursour.mobile.data.local.AppDatabase
import cl.oursour.mobile.data.local.entity.ConteoMetrica
import cl.oursour.mobile.data.local.entity.TipoEvento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Métricas agregadas a partir de los eventos guardados en Room
class MetricasViewModel(application: Application) : AndroidViewModel(application) {

    private val eventoDao = AppDatabase.obtener(application).eventoBusquedaDao()
    private val unDia = 24 * 60 * 60 * 1000L

    val ingredientesTop: Flow<List<ConteoMetrica>> = eventoDao.obtenerRanking(TipoEvento.INGREDIENTE, 5)
    val recetasTop: Flow<List<ConteoMetrica>> = eventoDao.obtenerRanking(TipoEvento.RECETA, 5)
    val combinacionesTop: Flow<List<ConteoMetrica>> = eventoDao.obtenerRanking(TipoEvento.COMBINACION, 5)
    val totalBusquedas: Flow<Int> = eventoDao.contar(TipoEvento.BUSQUEDA)

    // Cantidad de búsquedas de cada uno de los últimos 7 días
    val busquedasPorDia: Flow<List<ConteoMetrica>> =
        eventoDao.obtenerFechas(TipoEvento.BUSQUEDA, inicioDelDia(6)).map { fechas ->
            val formatoDia = SimpleDateFormat("EEE", Locale("es", "CL"))
            (6 downTo 0).map { diasAtras ->
                val inicio = inicioDelDia(diasAtras)
                val cantidad = fechas.count { it >= inicio && it < inicio + unDia }
                ConteoMetrica(formatoDia.format(inicio), cantidad)
            }
        }

    private fun inicioDelDia(diasAtras: Int): Long {
        val calendario = Calendar.getInstance()
        calendario.add(Calendar.DAY_OF_YEAR, -diasAtras)
        calendario.set(Calendar.HOUR_OF_DAY, 0)
        calendario.set(Calendar.MINUTE, 0)
        calendario.set(Calendar.SECOND, 0)
        calendario.set(Calendar.MILLISECOND, 0)
        return calendario.timeInMillis
    }

    fun reiniciarMetricas() {
        viewModelScope.launch { eventoDao.eliminarTodos() }
    }
}
