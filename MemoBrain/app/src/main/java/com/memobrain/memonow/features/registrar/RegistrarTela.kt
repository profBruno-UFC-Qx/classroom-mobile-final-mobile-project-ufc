package com.memobrain.memonow.features.registrar


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.painterResource
import com.memobrain.memonow.R

@Composable
fun RegistrarTela() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F8FB))
                .padding(innerPadding)
        ){
            Column (modifier = Modifier
                .padding(start = 35.dp, end = 35.dp, top = 50.dp, bottom = 40.dp)
                .fillMaxSize()
            ){
                Box(modifier = Modifier.fillMaxWidth().height(100.dp)
                ){
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row() { Text(text = "Crie sua conta", fontSize = 27.sp) }
                        Row() { Text(text = "Crie e memorize de diversas formas!") }
                    }
                }
                //Campo de Email
                var email by remember { mutableStateOf("") }
                Box(modifier = Modifier.padding(bottom =  5.dp).fillMaxWidth().height(70.dp)){
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(bottom = 2.dp)) {
                            Text(text = "E-mail", fontSize = 15.sp)
                        }
                        Row() {
                            BasicTextField(
                                value = email,
                                onValueChange = { email = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .background(Color.White),
                                textStyle = TextStyle(fontSize = 15.sp),
                                decorationBox = { innerTextField ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            if (email.isEmpty()) {
                                                Text(
                                                    text = "Digite seu e-mail",
                                                    color = Color.Gray,
                                                    fontSize = 16.sp
                                                )
                                            }
                                            innerTextField()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
                //Número de Telefone
                var telefone by remember { mutableStateOf("") }
                Box(modifier = Modifier.padding(bottom =  5.dp).fillMaxWidth().height(70.dp)) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Número",
                            fontSize = 15.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        BasicTextField(
                            value = telefone,
                            onValueChange = { novoTexto ->
                                if (novoTexto.all { it.isDigit() }) {
                                    telefone = novoTexto
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .background(Color.White),
                            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            decorationBox = { innerTextField ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "+55",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight(0.4f)
                                            .width(1.dp)
                                            .border(0.5.dp, Color(0xFFE2E8F0))
                                    )

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Box(modifier = Modifier.weight(1f)) {
                                        if (telefone.isEmpty()) {
                                            Text(
                                                text = "Digite seu telefone",
                                                color = Color(0xFFA0AEC0),
                                                fontSize = 16.sp
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            }
                        )
                    }
                }
                //Campo de Senha
                var senha by remember { mutableStateOf("") }
                var senhaVisivel by remember { mutableStateOf(false) }
                Box(modifier = Modifier.padding(bottom =  5.dp).fillMaxWidth().height(70.dp)){
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Senha",
                            fontSize = 15.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )

                        BasicTextField(
                            value = senha,
                            onValueChange = { senha = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .background(Color.White),
                            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),

                            visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),

                            decorationBox = { innerTextField ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        if (senha.isEmpty()) {
                                            Text(
                                                text = "Digite sua senha",
                                                color = Color(0xFFA0AEC0),
                                                fontSize = 16.sp
                                            )
                                        }
                                        innerTextField()
                                    }

                                    val icone = if (senhaVisivel) painterResource(id = R.drawable.olho_aberto) else painterResource(id = R.drawable.olho_fechado)
                                    val descricao = if (senhaVisivel) "Esconder senha" else "Mostrar senha"

                                    Icon(
                                        painter = icone,
                                        contentDescription = descricao,
                                        tint = Color(0xFF1A1A1A),
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable {
                                                senhaVisivel = !senhaVisivel
                                            }
                                    )
                                }
                            }
                        )
                    }
                }
                //Lembrar de Mim | Esqueci a senha
                var lembrarMeChecked by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth().height(50.dp)){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { lembrarMeChecked = !lembrarMeChecked }
                        ) {
                            Checkbox(
                                checked = lembrarMeChecked,
                                onCheckedChange = { lembrarMeChecked = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF1E3A8A),
                                    uncheckedColor = Color(0xFFCBD5E1)
                                ),
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Lembrar-me",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF475569)
                            )
                        }
                        Text(
                            text = "Esqueci a senha?",
                            fontSize = 14.sp,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.clickable {
                            }
                        )
                    }
                }
                //Botão de Cadastrar
                Box(modifier = Modifier.padding(top = 40.dp).fillMaxWidth().height(80.dp)){
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1B4363),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Criar Conta",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                //Google Apple Facebook
                /*
                Box(modifier = Modifier.fillMaxWidth().height(200.dp)){
                }
                */

            }
        }
    }
}