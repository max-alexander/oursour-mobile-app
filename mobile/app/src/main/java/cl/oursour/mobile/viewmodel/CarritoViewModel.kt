package cl.oursour.mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.oursour.mobile.data.local.AppDatabase
import cl.oursour.mobile.data.local.entity.CarritoItemEntity
import cl.oursour.mobile.data.repository.CatalogoRepository
import cl.oursour.mobile.model.Producto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// Toda la lógica del carro de compras. El carro se guarda en Room,
// por lo que se mantiene aunque se cierre la aplicación.
class CarritoViewModel(application: Application) : AndroidViewModel(application) {

    private val carritoDao = AppDatabase.obtener(application).carritoDao()

    val items: Flow<List<CarritoItemEntity>> = carritoDao.obtenerTodos()
    val unidades: Flow<Int> = carritoDao.contarUnidades()

    fun agregar(producto: Producto, cantidad: Int = 1) {
        viewModelScope.launch {
            val actual = carritoDao.buscarPorProducto(producto.id)
            val nuevaCantidad = ((actual?.cantidad ?: 0) + cantidad).coerceAtMost(producto.stock)
            if (nuevaCantidad > 0) {
                carritoDao.guardar(CarritoItemEntity(producto.id, producto.nombre, producto.precio, nuevaCantidad))
            }
        }
    }

    fun cambiarCantidad(item: CarritoItemEntity, nuevaCantidad: Int) {
        viewModelScope.launch {
            if (nuevaCantidad <= 0) {
                carritoDao.eliminar(item.productoId)
            } else {
                val stock = CatalogoRepository.buscarProducto(item.productoId)?.stock ?: nuevaCantidad
                carritoDao.guardar(item.copy(cantidad = nuevaCantidad.coerceAtMost(stock)))
            }
        }
    }

    fun eliminar(item: CarritoItemEntity) {
        viewModelScope.launch { carritoDao.eliminar(item.productoId) }
    }

    fun vaciar() {
        viewModelScope.launch { carritoDao.vaciar() }
    }

    fun calcularSubtotal(lista: List<CarritoItemEntity>): Int = lista.sumOf { it.precio * it.cantidad }
}
