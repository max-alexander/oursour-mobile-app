package com.example.oursour.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oursour.ui.theme.cafeClaro

@OptIn(ExperimentalMaterial3Api::class)  // habilita de características, componentes o funciones pertenecientes a la librería de Material Design 3
@Composable
fun HomeScreen() {
    Scaffold( // contenedor permite organizar rápida y estandarizada los elementos principales de una interfaz
        topBar = {  // Barra superior de la aplicación
            CenterAlignedTopAppBar(   // centrar el texto del topbar
                title = {
                    Text(
                        text = "Oursour",
                        fontWeight = FontWeight.Bold   // fuente de la letra
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cafeClaro,      // casilla contenedora
                    titleContentColor = Color.Black  // ahora es el titulo no el texto de relleno
                )
            )

        },
        floatingActionButton = {  // boton circular y flotante para la accion principal
            FloatingActionButton(
                onClick = { },
                containerColor = cafeClaro,
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "+",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = Color.White  // color de fondo de scaffold

    ) { innerPadding ->  // impide que el contenido quede detras de barras, menus
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(text = "Bienvenido")
        }
    }

}