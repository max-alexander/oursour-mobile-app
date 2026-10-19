package cl.oursour.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.oursour.mobile.data.local.entity.CarritoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {

    @Query("SELECT * FROM carrito ORDER BY nombre")
    fun obtenerTodos(): Flow<List<CarritoItemEntity>>

    // Suma de unidades, usada para el contador del icono del carro
    @Query("SELECT COALESCE(SUM(cantidad), 0) FROM carrito")
    fun contarUnidades(): Flow<Int>

    @Query("SELECT * FROM carrito WHERE productoId = :productoId LIMIT 1")
    suspend fun buscarPorProducto(productoId: Int): CarritoItemEntity?

    @Query("SELECT * FROM carrito")
    suspend fun obtenerLista(): List<CarritoItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(item: CarritoItemEntity)

    @Query("DELETE FROM carrito WHERE productoId = :productoId")
    suspend fun eliminar(productoId: Int)

    @Query("DELETE FROM carrito")
    suspend fun vaciar()
}
