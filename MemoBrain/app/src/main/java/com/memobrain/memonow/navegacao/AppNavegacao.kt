package com.memobrain.memonow.navegacao

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.memobrain.memonow.data.local.datastore.ArmazenamentoSessao
import com.memobrain.memonow.data.remote.autenticacao.ServicoLoginFirebase
import com.memobrain.memonow.features.cadernos.CadernosTelas
import com.memobrain.memonow.features.cadernos.CadernosTelas2
import com.memobrain.memonow.features.cadernos.DetalheCadernoScreen // 🟢 Importado aqui
import com.memobrain.memonow.features.login.LoginTela
import com.memobrain.memonow.features.login.TelaInicial
import com.memobrain.memonow.features.registrar.RegistrarTela
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

@Composable
fun AppNavegacao() {
    var telaAtual by remember { mutableStateOf(RotasTelas.INICIAL) }
    val context = LocalContext.current
    val armazenamentoSessao = remember { ArmazenamentoSessao(context) }
    var verificandoSessao by remember { mutableStateOf(true) }
    val servicoLogin = remember { ServicoLoginFirebase() }

    LaunchedEffect(Unit) {
        val usuarioFirebase = servicoLogin.obterUsuarioAtual()
        val sessao = armazenamentoSessao.sessaoFlow.first()

        if (sessao == null || usuarioFirebase == null) {
            armazenamentoSessao.limparSessao()
            telaAtual = RotasTelas.INICIAL
        } else {
            try {
                usuarioFirebase.reload().await()
                telaAtual = RotasTelas.HOME
            } catch (exception: Exception) {
                armazenamentoSessao.limparSessao()
                telaAtual = RotasTelas.INICIAL
            }
        }

        verificandoSessao = false
    }

    if (verificandoSessao) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text("Carregando...")
        }
    } else {
        when (telaAtual) {
            RotasTelas.INICIAL -> {
                TelaInicial(
                    entrar = { telaAtual = RotasTelas.LOGIN },
                    registrar = { telaAtual = RotasTelas.REGISTRAR },
                )
            }

            RotasTelas.LOGIN -> {
                LoginTela(
                    registrar = { telaAtual = RotasTelas.REGISTRAR },
                    onLoginSucesso = { telaAtual = RotasTelas.HOME },
                )
            }

            RotasTelas.REGISTRAR -> {
                RegistrarTela(
                    onCadastroSucesso = { telaAtual = RotasTelas.HOME },
                    onIrParaLogin = { telaAtual = RotasTelas.LOGIN },
                )
            }

            RotasTelas.HOME -> {
                CadernosTelas2(
                    onMetodoClick = { tituloMetodo ->
                        telaAtual = RotasTelas.CADERNOS
                    },
                    onAtividadeClick = { /* Lógica extra */ },
                    onCadernosBarClick = {
                        telaAtual = RotasTelas.CADERNOS
                    }
                )
            }

            RotasTelas.CADERNOS -> {
                CadernosTelas(
                    onInicioClick = {
                        telaAtual = RotasTelas.HOME
                    },
                    onCadernoClick = { idCaderno ->
                        // 🟢 CORREÇÃO: Direciona para os tópicos do caderno ao clicar no card
                        telaAtual = RotasTelas.DETALHE_CADERNO
                    }
                )
            }

            // 🟢 CORREÇÃO: Adicionada a nova rota gerenciando as ações de retorno e cliques nos tópicos
            RotasTelas.DETALHE_CADERNO -> {
                DetalheCadernoScreen(
                    onBackClick = {
                        telaAtual = RotasTelas.CADERNOS
                    },
                    onNovoArquivoClick = {
                        println("Novo arquivo clicado")
                    },
                    onTopicoClick = { tituloTopico ->
                        println("Topico clicado: $tituloTopico")
                    }
                )
            }
        }
    }
}