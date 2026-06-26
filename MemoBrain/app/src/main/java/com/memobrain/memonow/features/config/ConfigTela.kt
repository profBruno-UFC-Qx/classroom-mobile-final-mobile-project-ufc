package com.memobrain.memonow.features.perfil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.memobrain.memonow.R
import com.memobrain.memonow.features.cadernos.AbaMenu
import com.memobrain.memonow.features.cadernos.MenuInferiorMemonow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.tasks.await

@Composable
fun ConfigTela(
    onIrParaInicio: () -> Unit = {},
    onIrParaCadernos: () -> Unit = {},
    onSair: () -> Unit = {},
    onContaExcluida: () -> Unit = {},
) {
    val autenticacao = remember {
        FirebaseAuth.getInstance()
    }

    val banco = remember {
        FirebaseFirestore.getInstance()
    }

    val usuario = autenticacao.currentUser

    var nomeUsuario by remember(usuario?.uid) {
        mutableStateOf(
            obterNomeExibicao(
                nome = usuario?.displayName.orEmpty(),
                email = usuario?.email.orEmpty(),
            ),
        )
    }

    var mostrarDialogoExclusao by remember {
        mutableStateOf(false)
    }

    var estaExcluindo by remember {
        mutableStateOf(false)
    }

    var mensagemErro by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(usuario?.uid) {
        val usuarioId = usuario?.uid ?: return@LaunchedEffect

        val documentoUsuario = runCatching {
            banco
                .collection("usuarios")
                .document(usuarioId)
                .get()
                .await()
        }.getOrNull()

        val nomeFirestore = documentoUsuario
            ?.getString("nome")
            .orEmpty()

        nomeUsuario = obterNomeExibicao(
            nome = nomeFirestore.ifBlank {
                usuario?.displayName.orEmpty()
            },
            email = usuario?.email.orEmpty(),
        )
    }

    val contaCriadaEm = formatarDataCriacao(
        usuario?.metadata?.creationTimestamp,
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            MenuInferiorMemonow(
                abaSelecionada = AbaMenu.PERFIL,
                onAbaClick = { aba ->
                    when (aba) {
                        AbaMenu.INICIO -> onIrParaInicio()
                        AbaMenu.CADERNOS -> onIrParaCadernos()
                        else -> Unit
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            Text(
                text = "Minha Conta",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp),
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.foto_cerebro,
                    ),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = nomeUsuario,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    Text(
                        text = contaCriadaEm,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Text(
                        text = "Free",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Text(
                text = "Configurações",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
            ) {
                Column {
                    ConfigItem("Configurações da conta")
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    ConfigItem("Configurar notificações")
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    ConfigItem("Planos")
                }
            }

            Text(
                text = "Outros",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
            ) {
                Column {
                    ConfigItem("Privacidade e confidencialidade")
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    ConfigItem("Sobre o app")
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    ConfigItem("Ajuda")
                }
            }

            mensagemErro?.let { erro ->
                Text(
                    text = erro,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            OutlinedButton(
                onClick = {
                    mostrarDialogoExclusao = true
                },
                enabled = !estaExcluindo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = "Deletar minha conta")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    autenticacao.signOut()
                    onSair()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                ),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = "Sair")
            }
        }
    }

    if (mostrarDialogoExclusao) {
        AlertDialog(
            onDismissRequest = {
                if (!estaExcluindo) {
                    mostrarDialogoExclusao = false
                }
            },
            title = {
                Text("Deletar conta?")
            },
            text = {
                Text(
                    "Essa ação remove o acesso à sua conta e não poderá ser desfeita.",
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val usuarioAtual = autenticacao.currentUser

                        if (usuarioAtual == null) {
                            mostrarDialogoExclusao = false
                            mensagemErro = "Nenhuma conta está logada."
                        } else {
                            estaExcluindo = true
                            mensagemErro = null

                            usuarioAtual
                                .delete()
                                .addOnSuccessListener {
                                    estaExcluindo = false
                                    mostrarDialogoExclusao = false
                                    onContaExcluida()
                                }
                                .addOnFailureListener { erro ->
                                    estaExcluindo = false
                                    mensagemErro = erro.message
                                        ?: "Não foi possível excluir a conta. Entre novamente e tente outra vez."
                                }
                        }
                    },
                    enabled = !estaExcluindo,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                ) {
                    Text(
                        text = if (estaExcluindo) {
                            "Excluindo..."
                        } else {
                            "Excluir"
                        },
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoExclusao = false
                    },
                    enabled = !estaExcluindo,
                ) {
                    Text("Cancelar")
                }
            },
        )
    }
}

@Composable
private fun ConfigItem(
    title: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
    }
}

private fun obterNomeExibicao(
    nome: String,
    email: String,
): String {
    val localePtBr = Locale.forLanguageTag("pt-BR")

    val base = nome.ifBlank {
        email.substringBefore("@")
    }.ifBlank {
        "Usuário"
    }

    return base
        .replace(".", " ")
        .replace("_", " ")
        .replace("-", " ")
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .joinToString(" ") { palavra ->
            palavra
                .lowercase(localePtBr)
                .replaceFirstChar {
                    it.titlecase(localePtBr)
                }
        }
}

private fun formatarDataCriacao(
    timestamp: Long?,
): String {
    if (timestamp == null) {
        return "Conta criada"
    }

    val formato = SimpleDateFormat(
        "MMMM-yyyy",
        Locale.forLanguageTag("pt-BR"),
    )

    return "Conta criada em ${formato.format(Date(timestamp))}"
}