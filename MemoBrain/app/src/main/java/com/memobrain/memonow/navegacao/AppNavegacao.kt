package com.memobrain.memonow.navegacao

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.memobrain.memonow.features.login.LoginTela
import com.memobrain.memonow.features.login.TelaInicial
import com.memobrain.memonow.features.registrar.RegistrarTela

@Composable
fun AppNavegacao(){
    var telaAtual by remember {mutableStateOf(RotasTelas.INICIAL)}

    when (telaAtual){
        RotasTelas.INICIAL -> {
            TelaInicial(
                entrar = {telaAtual = RotasTelas.LOGIN},
                registrar = {telaAtual = RotasTelas.REGISTRAR}
            )
        }

        RotasTelas.LOGIN -> {
            LoginTela(
                registrar = {telaAtual = RotasTelas.REGISTRAR}
            )
        }

        RotasTelas.REGISTRAR -> {
            RegistrarTela()
        }

    }

}