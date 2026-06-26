package com.memobrain.memonow.features.registrar

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.memobrain.memonow.R
import com.memobrain.memonow.data.remote.autenticacao.ServicoCadastroFirebase

@Composable
fun RegistrarTela(
    onCadastroSucesso: () -> Unit = {},
    onIrParaLogin: () -> Unit = {},
) {
    val servicoCadastro =
        remember {
            ServicoCadastroFirebase()
        }

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    var senhaVisivel by remember { mutableStateOf(false) }
    var mensagemCadastro by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(false) }

    var erroNome by remember { mutableStateOf<String?>(null) }
    var erroEmail by remember { mutableStateOf<String?>(null) }
    var erroTelefone by remember { mutableStateOf<String?>(null) }
    var erroSenha by remember { mutableStateOf<String?>(null) }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 20.dp,
                    bottom = 24.dp,
                ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Crie sua conta",
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Crie e memorize de diversas formas!",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "Nome completo",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp),
            )

            CampoTextoCadastro(
                valor = nome,
                placeholder = "Digite seu nome completo",
                erro = erroNome,
                onValorChange = {
                    nome = it
                    erroNome = null
                },
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "E-mail",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp),
            )

            CampoTextoCadastro(
                valor = email,
                placeholder = "Digite seu e-mail",
                erro = erroEmail,
                keyboardType = KeyboardType.Email,
                onValorChange = {
                    email = it
                    erroEmail = null
                },
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Número",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp),
            )

            BasicTextField(
                value = telefone,
                onValueChange = { novoTexto ->
                    if (novoTexto.all { it.isDigit() }) {
                        telefone = novoTexto
                        erroTelefone = null
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(12.dp),
                        ),
                textStyle =
                    TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .border(
                                    width = 1.dp,
                                    color =
                                        if (erroTelefone != null) {
                                            MaterialTheme.colorScheme.error
                                        } else {
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                ).padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "+55",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier =
                                Modifier
                                    .fillMaxHeight(0.45f)
                                    .width(1.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(
                            modifier = Modifier.weight(1f),
                        ) {
                            if (telefone.isEmpty()) {
                                Text(
                                    text = "Digite seu telefone",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    fontSize = 14.sp,
                                )
                            }

                            innerTextField()
                        }
                    }
                },
            )

            erroTelefone?.let { erro ->
                Text(
                    text = erro,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Senha",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicTextField(
                    value = senha,
                    onValueChange = {
                        senha = it
                        erroSenha = null
                    },
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(42.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(12.dp),
                            ),
                    textStyle =
                        TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    singleLine = true,
                    visualTransformation =
                        if (senhaVisivel) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                    decorationBox = { innerTextField ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .border(
                                        width = 1.dp,
                                        color =
                                            if (erroSenha != null) {
                                                MaterialTheme.colorScheme.error
                                            } else {
                                                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                    ).padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier.weight(1f),
                            ) {
                                if (senha.isEmpty()) {
                                    Text(
                                        text = "Digite sua senha",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        fontSize = 14.sp,
                                    )
                                }

                                innerTextField()
                            }
                        }
                    },
                )

                Spacer(modifier = Modifier.width(10.dp))

                Icon(
                    painter =
                        painterResource(
                            id =
                                if (senhaVisivel) {
                                    R.drawable.olho_aberto
                                } else {
                                    R.drawable.olho_fechado
                                },
                        ),
                    contentDescription =
                        if (senhaVisivel) {
                            "Esconder senha"
                        } else {
                            "Mostrar senha"
                        },
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier =
                        Modifier
                            .size(22.dp)
                            .clickable {
                                senhaVisivel = !senhaVisivel
                            },
                )
            }

            erroSenha?.let { erro ->
                Text(
                    text = erro,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            Button(
                onClick = {
                    erroNome = null
                    erroEmail = null
                    erroTelefone = null
                    erroSenha = null
                    mensagemCadastro = ""

                    when {
                        nome.trim().length < 2 -> {
                            erroNome = "Digite seu nome completo"
                        }

                        email.isBlank() || !email.contains("@") -> {
                            erroEmail = "Digite um e-mail válido"
                        }

                        telefone.isBlank() -> {
                            erroTelefone = "Digite seu telefone"
                        }

                        senha.length < 6 -> {
                            erroSenha = "A senha deve ter pelo menos 6 caracteres"
                        }

                        else -> {
                            carregando = true

                            servicoCadastro.cadastrarUsuario(
                                nome = nome,
                                email = email,
                                senha = senha,
                                telefone = telefone,
                                aoSucesso = {
                                    carregando = false
                                    onCadastroSucesso()
                                },
                                aoErro = { erro ->
                                    carregando = false
                                    mensagemCadastro = erro
                                },
                            )
                        }
                    }
                },
                enabled = !carregando,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
            ) {
                Text(
                    text =
                        if (carregando) {
                            "Criando conta..."
                        } else {
                            "Criar Conta"
                        },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (mensagemCadastro.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = mensagemCadastro,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                )

                Text(
                    text = "ou continue com",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )

                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                BotaoSocialCadastro(
                    icone = R.drawable.ic_google,
                    descricao = "Google",
                )

                BotaoSocialCadastro(
                    icone = R.drawable.ic_apple,
                    descricao = "Apple",
                )

                BotaoSocialCadastro(
                    icone = R.drawable.ic_facebook,
                    descricao = "Facebook",
                )
            }

            Spacer(modifier = Modifier.height(42.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Já tem uma conta? ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                )

                Text(
                    text = "Entrar",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier =
                        Modifier.clickable {
                            onIrParaLogin()
                        },
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun CampoTextoCadastro(
    valor: String,
    placeholder: String,
    erro: String?,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValorChange: (String) -> Unit,
) {
    BasicTextField(
        value = valor,
        onValueChange = onValorChange,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(42.dp)
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(12.dp),
                ),
        textStyle =
            TextStyle(
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
            ),
        keyboardOptions =
            KeyboardOptions(
                keyboardType = keyboardType,
            ),
        singleLine = true,
        decorationBox = { innerTextField ->
            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .border(
                            width = 1.dp,
                            color =
                                if (erro != null) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                },
                            shape = RoundedCornerShape(12.dp),
                        ).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    if (valor.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 14.sp,
                        )
                    }

                    innerTextField()
                }
            }
        },
    )

    erro?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
fun BotaoSocialCadastro(
    icone: Int,
    descricao: String,
) {
    Surface(
        modifier =
            Modifier
                .width(96.dp)
                .height(46.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 6.dp,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = icone),
                contentDescription = descricao,
                modifier = Modifier.size(26.dp),
            )
        }
    }
}