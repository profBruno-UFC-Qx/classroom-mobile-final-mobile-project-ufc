package com.memobrain.memonow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.memobrain.memonow.features.login.LoginTela
import com.memobrain.memonow.features.login.TelaInicial
import com.memobrain.memonow.features.registrar.RegistrarTela
import com.memobrain.memonow.ui.tema.MemonowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MemonowTheme {
                App()
            }
        }
    }
}

enum class TelaAtual{
    INICIAL,
    LOGIN,
    REGISTRAR
}

@Composable
fun App() {
    var telaAtual by remember {mutableStateOf(TelaAtual.INICIAL)}

    when(telaAtual) {
        TelaAtual.INICIAL -> {
            TelaInicial(
                entrar = {telaAtual = TelaAtual.LOGIN},
                registrar = {telaAtual = TelaAtual.REGISTRAR}
            )
        }


        TelaAtual.LOGIN -> {
            LoginTela(
                registrar = {telaAtual = TelaAtual.REGISTRAR}
            )
        }

        TelaAtual.REGISTRAR -> {
            RegistrarTela()
        }
    }

}

