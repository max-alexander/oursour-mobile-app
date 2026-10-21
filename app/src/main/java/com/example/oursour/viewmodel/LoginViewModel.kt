package com.example.oursour.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.oursour.model.Usuario


// por cada pantalla se crea un viewmodel


// esta clase hereda del viewmodel
// view model es una capa intermedia entre la vista y los datos (modelo)

class LoginViewModel: ViewModel() {

    //variable para el correo

    // esto es para que se vaya mostrando lo que se escribe
    // get y set con el valor que rellenas (importalo)
    var mail by mutableStateOf("")

    fun cambiarMail(nuevoMail: String){
        mail = nuevoMail
    }

    // para la contraseña

    var pass by mutableStateOf("")
    fun cambiarPass(nuevaPass: String){
        pass = nuevaPass
    }


    val usuarioCorrecto = Usuario("admin", "123")
    fun login(): Boolean {
        var validador = false

        if(mail == usuarioCorrecto.mail && pass == usuarioCorrecto.pass){
            validador = true
        }
        return validador


    }
}