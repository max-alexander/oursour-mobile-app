package cl.oursour.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.oursour.mobile.data.local.entity.PedidoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {

    // Devuelve el id generado para el nuevo pedido
    @Insert
    suspend fun insertar(pedido: PedidoEntity): Long

    @Query("SELECT * FROM pedidos WHERE usuarioEmail = :email ORDER BY fecha DESC")
    fun obtenerPorUsuario(email: String): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedidos WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): PedidoEntity?
}
