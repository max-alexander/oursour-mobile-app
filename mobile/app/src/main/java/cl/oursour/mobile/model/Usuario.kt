package cl.oursour.mobile.model

enum class Rol {
    CONSUMIDOR,
    ADMINISTRADOR,
    INVITADO
}

data class Usuario(
    val nombre: String,
    val email: String,
    val rol: Rol
)
