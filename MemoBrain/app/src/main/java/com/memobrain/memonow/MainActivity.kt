package com.memobrain.memonow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.memobrain.memonow.features.login.TelaInicial
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

@Composable
fun App() {
    Scaffold { padding ->

        TelaInicial(modifier = Modifier.padding(padding))
        //LoginTela(modifier = Modifier.padding(padding))
        //CadernosTelas(modifier = Modifier.padding(padding))
        //CadernosTelas2(modifier = Modifier.padding(padding))
        //RegistrarTela(modifier = Modifier.padding(padding))
    }
}