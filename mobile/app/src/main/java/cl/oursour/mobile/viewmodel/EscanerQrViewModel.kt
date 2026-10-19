package cl.oursour.mobile.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.oursour.mobile.data.local.AppDatabase
import cl.oursour.mobile.data.local.entity.IngredienteFrecuenteEntity
import cl.oursour.mobile.data.repository.CatalogoRepository
import kotlinx.coroutines.launch

// Posibles resultados al leer un código QR
sealed class ResultadoQr {
    data class AbrirProducto(val productoId: Int) : ResultadoQr()
    data class AbrirReceta(val recetaId: Int) : ResultadoQr()
    data class IngredienteGuardado(val nombre: String) : ResultadoQr()
    data class NoReconocido(val contenido: String) : ResultadoQr()
}

// Los QR de Our Sour tienen el formato "OURSOUR:<TIPO>:<ID>"
// Ej: OURSOUR:PRODUCTO:1, OURSOUR:RECETA:4, OURSOUR:INGREDIENTE:11
class EscanerQrViewModel(application: Application) : AndroidViewModel(application) {

    private val frecuenteDao = AppDatabase.obtener(application).ingredienteFrecuenteDao()

    var ultimoResultado by mutableStateOf<ResultadoQr?>(null)
        private set

    fun procesarCodigo(contenido: String): ResultadoQr {
        val partes = contenido.trim().split(":")
        val id = partes.getOrNull(2)?.toIntOrNull()

        val resultado = if (partes.size == 3 && partes[0] == "OURSOUR" && id != null) {
            when (partes[1]) {
                "PRODUCTO" -> if (CatalogoRepository.buscarProducto(id) != null) ResultadoQr.AbrirProducto(id) else ResultadoQr.NoReconocido(contenido)
                "RECETA" -> if (CatalogoRepository.buscarReceta(id) != null) ResultadoQr.AbrirReceta(id) else ResultadoQr.NoReconocido(contenido)
                "INGREDIENTE" -> guardarIngrediente(id, contenido)
                else -> ResultadoQr.NoReconocido(contenido)
            }
        } else {
            ResultadoQr.NoReconocido(contenido)
        }

        ultimoResultado = resultado
        return resultado
    }

    // Un QR de ingrediente lo agrega directamente a "Mis ingredientes frecuentes" (Room)
    private fun guardarIngrediente(id: Int, contenido: String): ResultadoQr {
        val ingrediente = CatalogoRepository.buscarIngrediente(id) ?: return ResultadoQr.NoReconocido(contenido)
        viewModelScope.launch {
            frecuenteDao.insertar(IngredienteFrecuenteEntity(ingrediente.id, ingrediente.nombre, ingrediente.categoria.etiqueta))
        }
        return ResultadoQr.IngredienteGuardado(ingrediente.nombre)
    }
}
