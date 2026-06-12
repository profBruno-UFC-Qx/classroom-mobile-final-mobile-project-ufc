package com.memobrain.memonow.features.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memobrain.memonow.R
import com.memobrain.memonow.data.remote.autenticacao.ServicoLoginFirebase

@Composable
fun LoginTela(
    registrar: () -> Unit = {},
    onLoginSucesso: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var mensagemLogin by remember { mutableStateOf("") }

    val servicoLogin = remember { ServicoLoginFirebase() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFEEF0F3),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(top = 70.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_memobrain_logo),
                contentDescription = "Logo MemoBrain",
                modifier = Modifier.size(90.dp),
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "MemoBrain",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2C2F36),
            )

            Spacer(modifier = Modifier.height(30.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Digite seu e-mail",
                        color = Color(0xFFA9AFB7),
                        fontSize = 14.sp,
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color(0xFF2C2F36),
                    ),
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Digite sua senha",
                        color = Color(0xFFA9AFB7),
                        fontSize = 14.sp,
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color(0xFF2C2F36),
                    ),
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Esqueci a senha?",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                color = Color(0xFFC3C7CD),
                fontSize = 12.sp,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    servicoLogin.loginUsuario(
                        email = email,
                        senha = senha,
                        aoSucesso = { resultado ->
                            mensagemLogin = "Login realizado com sucesso: ${resultado.email}"
                            onLoginSucesso()
                        },
                        aoErro = { erro ->
                            mensagemLogin = erro
                        },
                    )
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2B5F87),
                    ),
            ) {
                Text(
                    text = "Entrar",
                    color = Color.White,
                    fontSize = 15.sp,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (mensagemLogin.isNotEmpty()) {
                Text(
                    text = mensagemLogin,
                    color = Color(0xFF2C2F36),
                    fontSize = 13.sp,
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFD9DDE2),
                )

                Text(
                    text = "ou continue com",
                    color = Color(0xFFC3C7CD),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFD9DDE2),
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                BotaoSocial(R.drawable.ic_google, "Google")
                Spacer(modifier = Modifier.width(16.dp))
                BotaoSocial(R.drawable.ic_apple, "Apple")
                Spacer(modifier = Modifier.width(16.dp))
                BotaoSocial(R.drawable.ic_facebook, "Facebook")
            }

            Spacer(modifier = Modifier.height(54.dp))

            Text(
                text = "Se você não tem uma conta",
                color = Color(0xFFA9AFB7),
                fontSize = 13.sp,
            )

            Row {
                Text(
                    text = "você pode ",
                    color = Color(0xFFA9AFB7),
                    fontSize = 13.sp,
                )

                Text(
                    text = "Registrar aqui",
                    color = Color(0xFF2A5A82),
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { registrar() },
                )
            }
        }
    }
}

@Composable
fun BotaoSocial(
    icone: Int,
    descricao: String,
) {
    Surface(
        modifier =
            Modifier
                .width(86.dp)
                .height(52.dp),
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 6.dp,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = icone),
                contentDescription = descricao,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}
