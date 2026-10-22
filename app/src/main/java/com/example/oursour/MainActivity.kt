package com.example.oursour

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.oursour.navigation.AppNavigation
import com.example.oursour.ui.theme.OursourTheme
import com.example.oursour.view.LoginScreen

class MainActivity : ComponentActivity() {
    // fun oncreate se encarga de levantar el proyecto
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OursourTheme {
                //aca llamas a las view
                AppNavigation()

            }
        }
    }
}



//el emulador es un telefono pero que vive dentro del pc
//activity = pantalla
