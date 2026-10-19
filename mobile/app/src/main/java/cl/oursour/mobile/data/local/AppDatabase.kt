package cl.oursour.mobile.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.oursour.mobile.data.local.dao.CarritoDao
import cl.oursour.mobile.data.local.dao.EventoBusquedaDao
import cl.oursour.mobile.data.local.dao.IngredienteFrecuenteDao
import cl.oursour.mobile.data.local.dao.PedidoDao
import cl.oursour.mobile.data.local.dao.UsuarioDao
import cl.oursour.mobile.data.local.entity.CarritoItemEntity
import cl.oursour.mobile.data.local.entity.EventoBusquedaEntity
import cl.oursour.mobile.data.local.entity.IngredienteFrecuenteEntity
import cl.oursour.mobile.data.local.entity.PedidoEntity
import cl.oursour.mobile.data.local.entity.UsuarioEntity

@Database(
    entities = [
        UsuarioEntity::class,
        IngredienteFrecuenteEntity::class,
        CarritoItemEntity::class,
        PedidoEntity::class,
        EventoBusquedaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun ingredienteFrecuenteDao(): IngredienteFrecuenteDao
    abstract fun carritoDao(): CarritoDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun eventoBusquedaDao(): EventoBusquedaDao

    companion object {
        // Una sola instancia de la base de datos para toda la app (patrón Singleton)
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                val nueva = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "oursour.db"
                ).build()
                instancia = nueva
                nueva
            }
        }
    }
}
