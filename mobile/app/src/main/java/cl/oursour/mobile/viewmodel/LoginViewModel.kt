package cl.oursour.mobile.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.oursour.mobile.data.local.AppDatabase
import cl.oursour.mobile.data.repository.SesionUsuario
import cl.oursour.mobile.model.Rol
import cl.oursour.mobile.model.Usuario
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao = AppDatabase.obtener(application).usuarioDao()

    // Cuenta de administrador de prueba (en la Unidad 3 la validará el backend con JWT y roles)
    private val adminValido = Usuario("Equipo Our Sour", "admin@oursour.cl", Rol.ADMINISTRADOR)
    private val passAdmin = "admin123"

    // ESTADOS DE PANTALLA
    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun cambiarEmail(nuevoEmail: String) {
        email = nuevoEmail.trim()
        error = null
    }

    fun cambiarPassword(nuevaPassword: String) {
        password = nuevaPassword
        error = null
    }

    fun iniciarSesion(onConsumidor: () -> Unit, onAdministrador: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            error = "Completa el correo y la contraseña"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            error = "El correo no tiene un formato válido"
            return
        }

        if (email.equals(adminValido.email, ignoreCase = true) && password == passAdmin) {
            SesionUsuario.iniciar(adminValido)
            onAdministrador()
            return
        }

        // Las consultas a Room se ejecutan dentro de una corrutina
        viewModelScope.launch {
            val usuario = usuarioDao.buscarPorEmail(email.lowercase())
            if (usuario != null && usuario.password == password) {
                SesionUsuario.iniciar(Usuario(usuario.nombre, usuario.email, Rol.CONSUMIDOR))
                onConsumidor()
            } else {
                error = "Correo o contraseña incorrectos"
            }
        }
    }
}
