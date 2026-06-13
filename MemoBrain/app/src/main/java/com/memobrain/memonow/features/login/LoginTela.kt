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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memobrain.memonow.R
import com.memobrain.memonow.data.local.datastore.ArmazenamentoSessao
import com.memobrain.memonow.data.remote.autenticacao.ServicoLoginFirebase
import kotlinx.coroutines.launch

@Composable
fun LoginTela(
    registrar: () -> Unit = {},
    onLoginSucesso: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var mensagemLogin by remember { mutableStateOf("") }
    var erroEmail by remember { mutableStateOf<String?>(null) }
    var erroSenha by remember { mutableStateOf<String?>(null) }

    val servicoLogin = remember { ServicoLoginFirebase() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val armazenamentoSessao = remember { ArmazenamentoSessao(context) }
    var carregando by remember { mutableStateOf(false) }

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
                onValueChange = {
                    email = it
                    erroEmail = null
                },
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
                isError = erroEmail != null,
                supportingText = {
                    erroEmail?.let {
                        Text(
                            text = it,
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp,
                        )
                    }
                },
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = if (erroEmail != null) Color(0xFFD32F2F) else Color.Transparent,
                        unfocusedBorderColor = if (erroEmail != null) Color(0xFFD32F2F) else Color.Transparent,
                        cursorColor = Color(0xFF2C2F36),
                    ),
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = {
                    senha = it
                    erroSenha = null
                },
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
                isError = erroSenha != null,
                supportingText = {
                    erroSenha?.let {
                        Text(
                            text = it,
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp,
                        )
                    }
                },
                visualTransformation = PasswordVisualTransformation(),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = if (erroSenha != null) Color(0xFFD32F2F) else Color.Transparent,
                        unfocusedBorderColor = if (erroSenha != null) Color(0xFFD32F2F) else Color.Transparent,
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
                    erroEmail = null
                    erroSenha = null
                    mensagemLogin = ""

                    if (email.isBlank()) {
                        erroEmail = "Digite seu email"
                        return@Button
                    }

                    if (senha.isBlank()) {
                        erroSenha = "Digite sua senha"
                        return@Button
                    }

                    carregando = true

                    servicoLogin.loginUsuario(
                        email = email,
                        senha = senha,
                        aoSucesso = { resultado ->
                            scope.launch {
                                armazenamentoSessao.salvarSessao(
                                    uid = resultado.uid,
                                    email = resultado.email,
                                )
                                carregando = false
                                mensagemLogin = "Login realizado com sucesso ${resultado.email}"
                                onLoginSucesso()
                            }
                        },
                        aoErro = { erro ->
                            erroEmail = "E-mail ou senha inválidos"
                            erroSenha = "E-mail ou senha inválidos"
                            carregando = false
                        },
                    )
                },
                enabled = !carregando,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF234E70),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF3A6F98),
                        disabledContentColor = Color.White,
                    ),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                shape = RoundedCornerShape(10.dp),
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
