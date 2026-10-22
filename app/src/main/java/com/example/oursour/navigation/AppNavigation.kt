package com.example.oursour.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.oursour.view.HomeScreen
import com.example.oursour.view.LoginScreen

// aqui consolidamos las pantallas, para que se comporten como aplicacion
// aca se consolida la nevagcion de una pantalla a la otra

@Composable
fun AppNavigation(){
    val navController = rememberNavController()  // recordar navegacion( devolver a pantalla anterior)

    //lugar donde se arrojan las rutas // encargado de consolidar pantallas
    NavHost (
        navController = navController,    // manera de comportamiento (remember)
        startDestination = Routes.LOGIN   // ruta de inicio
    ){
        // mostrar en pantalla las rutas de la aplicacion aqui estamos importando
        composable(Routes.LOGIN) {LoginScreen(navController)}   // login necesita navegar a la siguiente pantalla
        composable(Routes.Home) {HomeScreen()}  // cuando queramos que navega tambien debemos escribir el navcontroller

    }

}