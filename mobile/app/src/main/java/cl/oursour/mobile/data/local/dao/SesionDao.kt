package cl.oursour.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.oursour.mobile.data.local.entity.SesionEntity

@Dao
interface SesionDao {

    // REPLACE: si ya había una sesión guardada, la reemplaza por la nueva
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(sesion: SesionEntity)

    @Query("SELECT * FROM sesion WHERE id = 1 LIMIT 1")
    suspend fun obtener(): SesionEntity?

    @Query("DELETE FROM sesion")
    suspend fun borrar()
}
