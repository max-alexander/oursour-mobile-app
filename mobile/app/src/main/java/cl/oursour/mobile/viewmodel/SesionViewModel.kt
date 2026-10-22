package cl.oursour.mobile.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.oursour.mobile.data.local.AppDatabase
import cl.oursour.mobile.data.repository.SesionUsuario
import cl.oursour.mobile.model.Rol
import cl.oursour.mobile.model.Usuario
import cl.oursour.mobile.navigation.Routes
import kotlinx.coroutines.launch

// Revisa al abrir la app si existe la "marquita" de sesión en la base de datos local
class SesionViewModel(application: Application) : AndroidViewModel(application) {

    private val sesionDao = AppDatabase.obtener(application).sesionDao()

    // Pantalla con la que parte la app. Es null mientras se consulta la base de datos.
    var rutaInicial by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            val sesion = sesionDao.obtener()
            if (sesion != null) {
                // Hay marquita: se recupera el usuario y se entra directo
                val rol = Rol.valueOf(sesion.rol)
                SesionUsuario.iniciar(Usuario(sesion.nombre, sesion.email, rol))
                rutaInicial = if (rol == Rol.ADMINISTRADOR) Routes.ADMIN_PANEL else Routes.HOME
            } else {
                rutaInicial = Routes.BIENVENIDA
            }
        }
    }

    // Borra la marquita para que la próxima vez la app pida iniciar sesión
    fun cerrarSesion() {
        SesionUsuario.cerrar()
        viewModelScope.launch {
            sesionDao.borrar()
        }
    }
}
