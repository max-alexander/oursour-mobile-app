package cl.oursour.mobile.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.oursour.mobile.data.local.AppDatabase
import cl.oursour.mobile.data.local.entity.UsuarioEntity
import cl.oursour.mobile.data.repository.SesionUsuario
import cl.oursour.mobile.model.Rol
import cl.oursour.mobile.model.Usuario
import kotlinx.coroutines.launch

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao = AppDatabase.obtener(application).usuarioDao()

    var nombre by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var confirmarPassword by mutableStateOf("")
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun cambiarNombre(valor: String) {
        nombre = valor
        error = null
    }

    fun cambiarEmail(valor: String) {
        email = valor.trim()
        error = null
    }

    fun cambiarPassword(valor: String) {
        password = valor
        error = null
    }

    fun cambiarConfirmarPassword(valor: String) {
        confirmarPassword = valor
        error = null
    }

    // Devuelve un mensaje de error o null si el formulario es válido
    private fun validar(): String? {
        if (nombre.isBlank()) return "Ingresa tu nombre"
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "El correo no tiene un formato válido"
        if (password.length < 6) return "La contraseña debe tener al menos 6 caracteres"
        if (password != confirmarPassword) return "Las contraseñas no coinciden"
        return null
    }

    fun registrar(onExito: () -> Unit) {
        val mensaje = validar()
        if (mensaje != null) {
            error = mensaje
            return
        }

        viewModelScope.launch {
            val correo = email.lowercase()
            if (usuarioDao.buscarPorEmail(correo) != null) {
                error = "Ya existe una cuenta con ese correo"
                return@launch
            }
            // En la Unidad 3 el registro se hará en el backend (contraseña cifrada con BCrypt)
            usuarioDao.insertar(UsuarioEntity(correo, nombre.trim(), password))
            SesionUsuario.iniciar(Usuario(nombre.trim(), correo, Rol.CONSUMIDOR))
            onExito()
        }
    }
}
