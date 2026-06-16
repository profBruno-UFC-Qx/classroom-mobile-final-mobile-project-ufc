package com.memobrain.memonow.navegacao

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.memobrain.memonow.features.cadernos.CadernosTelas
import com.memobrain.memonow.features.cadernos.CadernosTelas2
import com.memobrain.memonow.features.login.LoginTela

@Composable
fun AppNavegacao() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RotasTelas.LOGIN
    ) {

        composable(RotasTelas.LOGIN) {
            LoginTela(
                modifier = Modifier
            )
        }

        composable(RotasTelas.CADERNOS) {
            CadernosTelas(
                modifier = Modifier
            )
        }

        composable(RotasTelas.INICIO) {
            CadernosTelas2(
                modifier = Modifier
            )
        }
    }
}