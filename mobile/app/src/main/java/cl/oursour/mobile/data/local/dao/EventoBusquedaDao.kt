package cl.oursour.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.oursour.mobile.data.local.entity.ConteoMetrica
import cl.oursour.mobile.data.local.entity.EventoBusquedaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoBusquedaDao {

    @Insert
    suspend fun insertar(eventos: List<EventoBusquedaEntity>)

    // Ranking de los valores más repetidos para un tipo de evento
    @Query(
        "SELECT valor, COUNT(*) AS total FROM eventos_busqueda " +
            "WHERE tipo = :tipo GROUP BY valor ORDER BY total DESC LIMIT :limite"
    )
    fun obtenerRanking(tipo: String, limite: Int): Flow<List<ConteoMetrica>>

    @Query("SELECT fecha FROM eventos_busqueda WHERE tipo = :tipo AND fecha >= :desde")
    fun obtenerFechas(tipo: String, desde: Long): Flow<List<Long>>

    @Query("SELECT COUNT(*) FROM eventos_busqueda WHERE tipo = :tipo")
    fun contar(tipo: String): Flow<Int>

    @Query("DELETE FROM eventos_busqueda")
    suspend fun eliminarTodos()
}
