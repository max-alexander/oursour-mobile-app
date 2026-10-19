package cl.oursour.mobile.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.oursour.mobile.data.local.AppDatabase
import cl.oursour.mobile.data.local.entity.PedidoEntity
import cl.oursour.mobile.data.repository.SesionUsuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PedidosViewModel(application: Application) : AndroidViewModel(application) {

    private val pedidoDao = AppDatabase.obtener(application).pedidoDao()

    // Solo los pedidos del usuario que inició sesión
    val pedidos: Flow<List<PedidoEntity>> = pedidoDao.obtenerPorUsuario(SesionUsuario.emailActual)

    var pedido by mutableStateOf<PedidoEntity?>(null)
        private set

    fun cargarPedido(id: Int) {
        viewModelScope.launch { pedido = pedidoDao.buscarPorId(id) }
    }
}
