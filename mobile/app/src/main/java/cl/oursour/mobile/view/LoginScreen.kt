package cl.oursour.mobile.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    modoAdmin: Boolean,
    onVolver: () -> Unit,
    onLoginConsumidor: () -> Unit,
    onLoginAdmin: () -> Unit,
    onIrARegistro: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    Scaffold(
        topBar = { OurSourTopBar(titulo = "", onVolver = onVolver) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título: letras en SP, el resto en DP
            Text(
                text = if (modoAdmin) "Acceso administrador" else "Iniciar sesión",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = loginViewModel.email,
                onValueChange = { loginViewModel.cambiarEmail(it) },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = loginViewModel.password,
                onValueChange = { loginViewModel.cambiarPassword(it) },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    // En cada intento el mensaje parte oculto
                    loginViewModel.textoVisible = false
                    val validador = loginViewModel.login()
                    if (validador) {
                        onLoginAdmin()
                    } else {
                        // Si no es el admin, se busca entre los clientes registrados;
                        // si tampoco existe, el ViewModel muestra el mensaje
                        loginViewModel.loginConsumidor(onLoginConsumidor)
                    }
                },
                modifier = Modifier.padding(top = 12.dp).fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(text = "Ingresar")
            }

            if (loginViewModel.textoVisible) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Credenciales inválidas",
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (modoAdmin) {
                Text(
                    text = "Cuenta de prueba: admin@oursour.cl / admin123",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                TextButton(
                    onClick = onIrARegistro,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(text = "¿No tienes cuenta? Regístrate")
                }
            }
        }
    }
}
