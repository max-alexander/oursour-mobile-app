package cl.oursour.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.oursour.mobile.data.local.entity.IngredienteFrecuenteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredienteFrecuenteDao {

    // Flow: la pantalla se actualiza sola cada vez que cambia la tabla
    @Query("SELECT * FROM ingredientes_frecuentes ORDER BY nombre")
    fun obtenerTodos(): Flow<List<IngredienteFrecuenteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(ingrediente: IngredienteFrecuenteEntity)

    @Query("DELETE FROM ingredientes_frecuentes WHERE ingredienteId = :ingredienteId")
    suspend fun eliminar(ingredienteId: Int)

    @Query("DELETE FROM ingredientes_frecuentes")
    suspend fun eliminarTodos()
}
