package com.memobrain.memonow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.memobrain.memonow.features.cadernos.CadernosTelas2
import com.memobrain.memonow.features.cadernos.CadernosTelas
import com.memobrain.memonow.features.login.LoginTela
import com.memobrain.memonow.ui.tema.MemonowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // igual ao padrão do Android Studio

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

        // Aqui você troca as telas:
        CadernosTelas(modifier = Modifier.padding(padding))
        //CadernosTelas2(modifier = Modifier.padding(padding))
        //LoginTela(modifier = Modifier.padding(padding))
        //RegistrarTela(modifier = Modifier.padding(padding))

fn AppPreview(){
    App()
}