package cl.oursour.mobile.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cl.oursour.mobile.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

// DAO (Data Access Object): única capa que habla directo con la tabla "usuarios".
// Es una interfaz: las funciones no tienen cuerpo, KSP genera el código a partir de las anotaciones.
@Dao
interface UsuarioDao {

    // suspend: operaciones que se pueden demorar, se ejecutan dentro de una corrutina
    @Insert
    suspend fun insertar(usuario: UsuarioEntity)

    @Update
    suspend fun actualizar(usuario: UsuarioEntity)

    @Delete
    suspend fun eliminar(usuario: UsuarioEntity)

    // Devuelve un Flow (no lleva suspend): los usuarios van saliendo por este "tubito"
    @Query("SELECT * FROM usuarios ORDER BY nombre")
    fun listar(): Flow<List<UsuarioEntity>>

    // :email y :password se reemplazan por los parámetros de la función (deben llamarse igual)
    @Query(
        """
        SELECT * FROM usuarios
        WHERE email = :email AND password = :password
        LIMIT 1
        """
    )
    suspend fun login(email: String, password: String): UsuarioEntity?

    // Busca por la llave primaria; null si no existe
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): UsuarioEntity?
}
