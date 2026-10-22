package cl.oursour.mobile.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.oursour.mobile.view.components.OurSourTopBar
import cl.oursour.mobile.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    onVolver: () -> Unit,
    onRegistroExitoso: () -> Unit,
    registroViewModel: RegistroViewModel = viewModel()
) {
    Scaffold(
        topBar = { OurSourTopBar(titulo = "", onVolver = onVolver) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título: letras en SP, el resto en DP
            Text(
                text = "Crear cuenta",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = registroViewModel.nombre,
                onValueChange = { registroViewModel.cambiarNombre(it) },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = registroViewModel.email,
                onValueChange = { registroViewModel.cambiarEmail(it) },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = registroViewModel.password,
                onValueChange = { registroViewModel.cambiarPassword(it) },
                label = { Text("Contraseña (mínimo 6 caracteres)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = registroViewModel.confirmarPassword,
                onValueChange = { registroViewModel.cambiarConfirmarPassword(it) },
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            registroViewModel.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }

            Button(
                onClick = { registroViewModel.registrar(onRegistroExitoso) },
                modifier = Modifier.padding(top = 12.dp).fillMaxWidth()
            ) {
                Text(text = "Crear cuenta")
            }
        }
    }
}
