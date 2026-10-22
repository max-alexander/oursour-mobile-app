package com.example.oursour.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.oursour.navigation.Routes
import com.example.oursour.ui.theme.cafeClaro
import com.example.oursour.viewmodel.LoginViewModel

// vista del login

//anotacion que le indica a la funcion que se debe posicionar en pantalla
//to do lo que esta en su interior se muestra en pantalla

@Composable
// para que la pantalla pueda usarse en cualqueir parte y ue siempre este con su controlador(viewmodel)
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel() // parametro que ya tiene valor
                                                 // en rojo si falta dependencias
) {
        // parentesis propiedades que modifican su estructura ()
        // llaves corresponden al contenido (lo que el usuario va a ver dentro de esta columna {}

        //column apila elementos uno debajo del otro
    Column(
            // centrado de lado a lado
        horizontalAlignment = Alignment.CenterHorizontally,
            // centrado de arriba a abajo
        verticalArrangement = Arrangement.Center,
            // propiedades comunes (tamaño, margen, ancho, alto, padding)
            // ocupá to do el espacio disponible, a lo ancho y a lo alto
        modifier = Modifier.fillMaxSize().padding(16.dp)  // padding limita el espacio

    ) {
        Text(
            text = "Bienvenido a OurSour",
            // tamaño del texto
            fontSize = 28.sp,   // sp es tamaño independiente de la escala
            fontWeight = FontWeight.Bold,  // fuente de letra, ancha, negritas, delgada
            modifier = Modifier.padding(bottom = 20.dp)  // separacion del titulo
        )
            //caja de texto
        OutlinedTextField(
            value = loginViewModel.mail,
            onValueChange = {
                loginViewModel.cambiarMail(it) // it representa el valor de la caja de texto
            },
            label = {
                Text(text = "Correo")
            },
            // fillmaxwidth ocupa to do el ancho disponible
            modifier = Modifier.fillMaxWidth()

        )
            //segunda caja de texto igual a la de arriba
        OutlinedTextField(
            value = loginViewModel.pass,
            onValueChange = {
                loginViewModel.cambiarPass(it) // it mencion a la caja de texto (check)
            },
            label = {
                Text(text = "Contraseña")
            },

            modifier = Modifier.fillMaxWidth(),

            // para ocultar la contraseña
            visualTransformation = PasswordVisualTransformation()
        )
        //boton
        Button(
            onClick = {
                println("bmp: Botón Presionado...") // iniciales para filtrar en logcat
                println(loginViewModel.mail)
                println(loginViewModel.pass)     // esto guarda el registro textual en logcat

                // validar login para mostrar o no el mensaje de error
                loginViewModel.textoVisible = false  // con esto el texto desaparece si inicias bien

                val validador = loginViewModel.login()

                if(validador){
                    //navegar
                    navController.navigate(Routes.Home)

                }else{
                    loginViewModel.textoVisible = true
                }
            },
            //fill max width ocupa to do el ancho disponible
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth().height(50.dp),  // dp es densidad de pixel
            shape = RoundedCornerShape(5.dp),  // redondea los bordes ( en este caso los hace mas cuadrados xd)
            colors = ButtonDefaults.buttonColors(
                containerColor = cafeClaro,     // color casilla contenedora
                contentColor = Color.Black,     // color del texto dentro de la casilla
            )
        ) {
            Text(text = "Ingresar", fontSize = 20.sp)  // texto boton


        }

        // texto para cunado los datos esten mal

        if (loginViewModel.textoVisible) {
            Text(
                text = "Credenciales Inválidas",
                fontSize = 20.sp,  // sp pixeles independientes de la escala
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp),
                color = Color.Red
            )
        }
    }
}