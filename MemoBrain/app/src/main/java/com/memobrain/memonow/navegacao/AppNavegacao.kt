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
import com.memobrain.memonow.features.cadernos.DetalheCadernoScreen
import com.memobrain.memonow.features.cadernos.DashboardCadernosTela
import com.memobrain.memonow.features.cadernos.ListaCadernosTela
import com.memobrain.memonow.features.cadernos.EditNotebookScreen
import com.memobrain.memonow.features.cadernos.EditNotebookViewModel
import com.memobrain.memonow.features.cadernos.EditArquivoScreen
import com.memobrain.memonow.features.cadernos.EditArquivoViewModel
import com.memobrain.memonow.features.cadernos.CreateFlashcardScreen
import com.memobrain.memonow.features.cadernos.CreateFlashcardViewModel
import com.memobrain.memonow.features.cadernos.PlayFlashcardScreen
import com.memobrain.memonow.features.cadernos.PlayFlashcardViewModel
import com.memobrain.memonow.features.cadernos.CriarCadernoScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.memobrain.memonow.features.cadernos.CriarArquivoScreen
import com.memobrain.memonow.features.cadernos.QuizScreen
import com.memobrain.memonow.features.login.LoginTela
import com.memobrain.memonow.features.login.TelaInicial
import com.memobrain.memonow.features.registrar.RegistrarTela
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

@Composable
fun AppNavegacao() {
    var telaAtual by remember { mutableStateOf(RotasTelas.INICIAL) }

    // 🟢 Nova variável para guardar o ID do arquivo clicado!
    var idArquivoSelecionado by remember { mutableStateOf("") }

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
                telaAtual = RotasTelas.INICIO_APP
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
                    onLoginSucesso = { telaAtual = RotasTelas.INICIO_APP },
                )
            }

            RotasTelas.REGISTRAR -> {
                RegistrarTela(
                    onCadastroSucesso = { telaAtual = RotasTelas.INICIO_APP },
                    onIrParaLogin = { telaAtual = RotasTelas.LOGIN },
                )
            }

            RotasTelas.HOME -> {
                DashboardCadernosTela(
                    onIrParaCadernos = { telaAtual = RotasTelas.CADERNOS },
                    onMetodoClick = { metodo ->
                        if (metodo == "Flashcard") {
                            telaAtual = RotasTelas.FLASHCARD
                        }
                    }
                )
            }

            RotasTelas.INICIO_APP -> {
                DashboardCadernosTela(
                    onIrParaCadernos = { telaAtual = RotasTelas.CADERNOS },
                    onMetodoClick = { metodo ->
                        if (metodo == "Flashcard") {
                            telaAtual = RotasTelas.FLASHCARD
                        }
                    }
                )
            }

            RotasTelas.CADERNOS -> {
                ListaCadernosTela(
                    onIrParaInicio = { telaAtual = RotasTelas.INICIO_APP },
                    onCadernoClick = { idCaderno ->
                        telaAtual = RotasTelas.DETALHE_CADERNO
                    },
                    onEditarClick = { idCaderno ->
                        telaAtual = RotasTelas.EDITAR_CADERNO
                    },
                    onNovoCadernoClick = {
                        telaAtual = RotasTelas.CRIAR_CADERNO
                    }
                )
            }

            RotasTelas.EDITAR_CADERNO -> {
                val editViewModel: EditNotebookViewModel = viewModel()
                EditNotebookScreen(
                    viewModel = editViewModel,
                    onNavigateBack = {
                        telaAtual = RotasTelas.CADERNOS
                    }
                )
            }

            RotasTelas.DETALHE_CADERNO -> {
                DetalheCadernoScreen(
                    onBackClick = {
                        telaAtual = RotasTelas.CADERNOS
                    },
                    onNovoArquivoClick = {
                        telaAtual = RotasTelas.CRIAR_ARQUIVO
                    },
                    onTopicoClick = { tituloClicado ->
                        if (tituloClicado == "Introdução") {
                            telaAtual = RotasTelas.PLAY_FLASHCARD
                        } else {
                            // 🟢 Restaura a navegação para Múltipla Escolha nos outros cards
                            telaAtual = RotasTelas.QUIZ_MULTIPLA_ESCOLHA
                        }
                    },
                    onEditarArquivoClick = { idOuTitulo ->
                        idArquivoSelecionado = idOuTitulo
                        telaAtual = RotasTelas.EDITAR_ARQUIVO
                    }
                )
            }

            RotasTelas.EDITAR_ARQUIVO -> {
                val editArquivoViewModel: EditArquivoViewModel = viewModel()
                EditArquivoScreen(
                    viewModel = editArquivoViewModel,
                    onNavigateBack = {
                        telaAtual = RotasTelas.DETALHE_CADERNO
                    }
                )
            }

            RotasTelas.CRIAR_CADERNO -> {
                CriarCadernoScreen(
                    onBackClick = {
                        telaAtual = RotasTelas.CADERNOS
                    }
                )
            }

            RotasTelas.CRIAR_ARQUIVO -> {
                CriarArquivoScreen(
                    onBackClick = {
                        telaAtual = RotasTelas.DETALHE_CADERNO
                    }
                )
            }

            RotasTelas.QUIZ_MULTIPLA_ESCOLHA -> {
                QuizScreen(
                    // TODO: Quando o colega for fazer o Firebase, ele vai descomentar a linha abaixo
                    // e atualizar a QuizScreen para receber esse parâmetro.
                    // arquivoId = idArquivoSelecionado,
                    onFecharClick = {
                        telaAtual = RotasTelas.DETALHE_CADERNO
                    }
                )
            }

            RotasTelas.FLASHCARD -> {
                val flashcardViewModel: CreateFlashcardViewModel = viewModel()
                CreateFlashcardScreen(
                    viewModel = flashcardViewModel,
                    onNavigateBack = {
                        telaAtual = RotasTelas.INICIO_APP
                    }
                )
            }

            RotasTelas.PLAY_FLASHCARD -> {
                val playViewModel: PlayFlashcardViewModel = viewModel()
                PlayFlashcardScreen(
                    viewModel = playViewModel,
                    onNavigateBack = {
                        telaAtual = RotasTelas.DETALHE_CADERNO
                    }
                )
            }
        }
    }
}