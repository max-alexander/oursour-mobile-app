package cl.oursour.mobile.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.oursour.mobile.data.local.dao.CarritoDao
import cl.oursour.mobile.data.local.dao.EventoBusquedaDao
import cl.oursour.mobile.data.local.dao.IngredienteFrecuenteDao
import cl.oursour.mobile.data.local.dao.PedidoDao
import cl.oursour.mobile.data.local.dao.SesionDao
import cl.oursour.mobile.data.local.dao.UsuarioDao
import cl.oursour.mobile.data.local.entity.CarritoItemEntity
import cl.oursour.mobile.data.local.entity.EventoBusquedaEntity
import cl.oursour.mobile.data.local.entity.IngredienteFrecuenteEntity
import cl.oursour.mobile.data.local.entity.PedidoEntity
import cl.oursour.mobile.data.local.entity.SesionEntity
import cl.oursour.mobile.data.local.entity.UsuarioEntity

@Database(
    entities = [
        UsuarioEntity::class,
        IngredienteFrecuenteEntity::class,
        CarritoItemEntity::class,
        PedidoEntity::class,
        EventoBusquedaEntity::class,
        SesionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun ingredienteFrecuenteDao(): IngredienteFrecuenteDao
    abstract fun carritoDao(): CarritoDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun eventoBusquedaDao(): EventoBusquedaDao
    abstract fun sesionDao(): SesionDao

    companion object {
        // Versión 1 -> 2: se agrega la tabla "sesion" sin borrar los datos que ya existían
        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `sesion` (`id` INTEGER NOT NULL, `email` TEXT NOT NULL, " +
                        "`nombre` TEXT NOT NULL, `rol` TEXT NOT NULL, PRIMARY KEY(`id`))"
                )
            }
        }

        // Una sola instancia de la base de datos para toda la app (patrón Singleton)
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                val nueva = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "oursour.db"
                ).addMigrations(MIGRACION_1_2).build()
                instancia = nueva
                nueva
            }
        }
    }
}
