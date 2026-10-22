package cl.oursour.mobile.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Pantalla 1 del wireframe: Login / Onboarding
@Composable
fun BienvenidaScreen(
    onIniciarSesion: () -> Unit,
    onInvitado: () -> Unit,
    onAccesoAdmin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo: círculo con el ícono de cóctel
        Box(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .size(110.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.LocalBar,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(60.dp)
            )
        }

        Text(
            text = "Our Sour",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "\"¡Encuentra tu cóctel perfecto!\"",
            fontSize = 18.sp,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Dinos qué ingredientes tienes en casa y te mostramos qué cócteles y mocktails puedes preparar.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onIniciarSesion,
            modifier = Modifier.padding(top = 48.dp).fillMaxWidth()
        ) {
            Text(text = "Iniciar sesión")
        }

        OutlinedButton(
            onClick = onInvitado,
            modifier = Modifier.padding(top = 12.dp).fillMaxWidth()
        ) {
            Text(text = "Usar como invitado")
        }

        // Empuja el acceso de administrador hacia abajo
        Spacer(Modifier.weight(1f))

        TextButton(onClick = onAccesoAdmin) {
            Text(text = "Acceso administrador")
        }
    }
}
