package cl.oursour.mobile.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.oursour.mobile.data.local.AppDatabase
import cl.oursour.mobile.data.local.entity.SesionEntity
import cl.oursour.mobile.data.repository.SesionUsuario
import cl.oursour.mobile.model.Rol
import cl.oursour.mobile.model.Usuario
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao = AppDatabase.obtener(application).usuarioDao()
    private val sesionDao = AppDatabase.obtener(application).sesionDao()

    // Cuenta de administrador de prueba (en la Unidad 3 la validará el backend con JWT y roles)
    private val adminValido = Usuario("Equipo Our Sour", "admin@oursour.cl", Rol.ADMINISTRADOR)
    private val passAdmin = "admin123"

    // ESTADOS DE PANTALLA
    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    // Controla si se muestra el mensaje "Credenciales inválidas"
    var textoVisible by mutableStateOf(false)

    fun cambiarEmail(nuevoEmail: String) {
        email = nuevoEmail.trim()
    }

    fun cambiarPassword(nuevaPassword: String) {
        password = nuevaPassword
    }

    // Valida las credenciales contra el usuario en duro (admin).
    // Un solo return, como sugiere la buena práctica.
    fun login(): Boolean {
        var validador = false
        if (email.equals(adminValido.email, ignoreCase = true) && password == passAdmin) {
            SesionUsuario.iniciar(adminValido)
            guardarSesion(adminValido)
            validador = true
        }
        return validador
    }

    // Valida a los clientes registrados en la base de datos local (Room).
    // Las consultas a Room se ejecutan dentro de una corrutina.
    fun loginConsumidor(onExito: () -> Unit) {
        viewModelScope.launch {
            // La consulta del DAO devuelve el usuario solo si el correo y la contraseña coinciden
            val usuario = usuarioDao.login(email.lowercase(), password)
            if (usuario != null) {
                val consumidor = Usuario(usuario.nombre, usuario.email, Rol.CONSUMIDOR)
                SesionUsuario.iniciar(consumidor)
                guardarSesion(consumidor)
                onExito()
            } else {
                textoVisible = true
            }
        }
    }

    // Guarda la "marquita" en la base de datos local para no pedir login al volver a abrir la app
    private fun guardarSesion(usuario: Usuario) {
        viewModelScope.launch {
            sesionDao.guardar(SesionEntity(email = usuario.email, nombre = usuario.nombre, rol = usuario.rol.name))
        }
    }
}
