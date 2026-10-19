package cl.oursour.mobile.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cl.oursour.mobile.model.Rol
import cl.oursour.mobile.model.Usuario

// Guarda en memoria quién está usando la app.
// En la Unidad 3 aquí también se guardará el token JWT entregado por el backend.
object SesionUsuario {

    var usuario by mutableStateOf<Usuario?>(null)
        private set

    val esAdministrador: Boolean
        get() = usuario?.rol == Rol.ADMINISTRADOR

    val esInvitado: Boolean
        get() = usuario?.rol == Rol.INVITADO

    // Email con el que se guardan los pedidos en la base de datos local
    val emailActual: String
        get() = usuario?.email ?: "invitado"

    fun iniciar(nuevoUsuario: Usuario) {
        usuario = nuevoUsuario
    }

    fun iniciarComoInvitado() {
        usuario = Usuario("Invitado", "invitado", Rol.INVITADO)
    }

    fun cerrar() {
        usuario = null
    }
}
