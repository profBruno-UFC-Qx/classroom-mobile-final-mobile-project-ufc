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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.memobrain.memonow.data.local.datastore.ArmazenamentoSessao
import com.memobrain.memonow.data.remote.autenticacao.ServicoLoginFirebase
import com.memobrain.memonow.features.cadernos.CreateFlashcardScreen
import com.memobrain.memonow.features.cadernos.CreateFlashcardViewModel
import com.memobrain.memonow.features.cadernos.CreateMultipleChoiceScreen
import com.memobrain.memonow.features.cadernos.CreateMultipleChoiceViewModel
import com.memobrain.memonow.features.cadernos.CriarArquivoScreen
import com.memobrain.memonow.features.cadernos.CriarCadernoScreen
import com.memobrain.memonow.features.cadernos.DashboardCadernosTela
import com.memobrain.memonow.features.cadernos.DetalheCadernoScreen
import com.memobrain.memonow.features.cadernos.EditArquivoScreen
import com.memobrain.memonow.features.cadernos.EditArquivoViewModel
import com.memobrain.memonow.features.cadernos.EditNotebookScreen
import com.memobrain.memonow.features.cadernos.EditNotebookViewModel
import com.memobrain.memonow.features.cadernos.ListaCadernosTela
import com.memobrain.memonow.features.cadernos.RevisarArquivoScreen
import com.memobrain.memonow.features.cadernos.RevisarArquivoViewModel
import com.memobrain.memonow.features.login.LoginTela
import com.memobrain.memonow.features.login.TelaInicial
import com.memobrain.memonow.features.registrar.RegistrarTela
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

@Composable
fun AppNavegacao() {
    var telaAtual by remember {
        mutableStateOf(RotasTelas.INICIAL)
    }

    var rotaRetornoAoCancelarCriacaoConteudo by remember {
        mutableStateOf(RotasTelas.DETALHE_CADERNO)
    }

    var idCadernoSelecionado by remember {
        mutableStateOf("")
    }

    var nomeCadernoSelecionado by remember {
        mutableStateOf("")
    }

    var idArquivoSelecionado by remember {
        mutableStateOf("")
    }

    var tituloArquivoSelecionado by remember {
        mutableStateOf("")
    }

    var descricaoArquivoSelecionado by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    val armazenamentoSessao =
        remember {
            ArmazenamentoSessao(context)
        }

    val servicoLogin =
        remember {
            ServicoLoginFirebase()
        }

    var verificandoSessao by remember {
        mutableStateOf(true)
    }

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
            Text(text = "Carregando...")
        }

        return
    }

    when (telaAtual) {
        RotasTelas.INICIAL -> {
            TelaInicial(
                entrar = {
                    telaAtual = RotasTelas.LOGIN
                },
                registrar = {
                    telaAtual = RotasTelas.REGISTRAR
                },
            )
        }

        RotasTelas.LOGIN -> {
            LoginTela(
                registrar = {
                    telaAtual = RotasTelas.REGISTRAR
                },
                onLoginSucesso = {
                    telaAtual = RotasTelas.INICIO_APP
                },
            )
        }

        RotasTelas.REGISTRAR -> {
            RegistrarTela(
                onCadastroSucesso = {
                    telaAtual = RotasTelas.INICIO_APP
                },
                onIrParaLogin = {
                    telaAtual = RotasTelas.LOGIN
                },
            )
        }

        RotasTelas.HOME,
        RotasTelas.INICIO_APP,
        -> {
            DashboardCadernosTela(
                onIrParaCadernos = {
                    telaAtual = RotasTelas.CADERNOS
                },
            )
        }

        RotasTelas.CADERNOS -> {
            ListaCadernosTela(
                onIrParaInicio = {
                    telaAtual = RotasTelas.INICIO_APP
                },
                onCadernoClick = { caderno ->
                    idCadernoSelecionado = caderno.id
                    nomeCadernoSelecionado = caderno.titulo
                    telaAtual = RotasTelas.DETALHE_CADERNO
                },
                onEditarClick = { cadernoId ->
                    idCadernoSelecionado = cadernoId
                    telaAtual = RotasTelas.EDITAR_CADERNO
                },
                onNovoCadernoClick = {
                    telaAtual = RotasTelas.CRIAR_CADERNO
                },
            )
        }

        RotasTelas.DETALHE_CADERNO -> {
            DetalheCadernoScreen(
                cadernoId = idCadernoSelecionado,
                nomeCaderno = nomeCadernoSelecionado,
                onBackClick = {
                    telaAtual = RotasTelas.CADERNOS
                },
                onNovoArquivoClick = {
                    telaAtual = RotasTelas.CRIAR_ARQUIVO
                },
                onTopicoClick = { arquivo ->
                    idArquivoSelecionado = arquivo.id
                    tituloArquivoSelecionado = arquivo.titulo
                    descricaoArquivoSelecionado = arquivo.descricao

                    telaAtual = RotasTelas.REVISAR_ARQUIVO
                },
                onEditarArquivoClick = { arquivoId ->
                    idArquivoSelecionado = arquivoId
                    telaAtual = RotasTelas.EDITAR_ARQUIVO
                },
            )
        }

        RotasTelas.EDITAR_CADERNO -> {
            val editViewModel: EditNotebookViewModel = viewModel()

            EditNotebookScreen(
                cadernoId = idCadernoSelecionado,
                viewModel = editViewModel,
                onNavigateBack = {
                    telaAtual = RotasTelas.CADERNOS
                },
            )
        }

        RotasTelas.EDITAR_ARQUIVO -> {
            val editArquivoViewModel: EditArquivoViewModel = viewModel()

            EditArquivoScreen(
                cadernoId = idCadernoSelecionado,
                arquivoId = idArquivoSelecionado,
                viewModel = editArquivoViewModel,
                onNavigateBack = {
                    telaAtual = RotasTelas.DETALHE_CADERNO
                },
            )
        }

        RotasTelas.CRIAR_CADERNO -> {
            CriarCadernoScreen(
                onBackClick = {
                    telaAtual = RotasTelas.CADERNOS
                },
            )
        }

        RotasTelas.CRIAR_ARQUIVO -> {
            CriarArquivoScreen(
                cadernoId = idCadernoSelecionado,
                onBackClick = {
                    telaAtual = RotasTelas.DETALHE_CADERNO
                },
                onArquivoCriado = { arquivo ->
                    idArquivoSelecionado = arquivo.id
                    tituloArquivoSelecionado = arquivo.titulo
                    descricaoArquivoSelecionado = arquivo.descricao

                    rotaRetornoAoCancelarCriacaoConteudo =
                        RotasTelas.DETALHE_CADERNO

                    telaAtual =
                        when (arquivo.metodo) {
                            "Múltipla Escolha" -> {
                                RotasTelas.CRIAR_MULTIPLA_ESCOLHA
                            }

                            else -> {
                                RotasTelas.CRIAR_PERGUNTA_ABERTA
                            }
                        }
                },
            )
        }

        RotasTelas.CRIAR_PERGUNTA_ABERTA -> {
            val perguntaAbertaViewModel: CreateFlashcardViewModel =
                viewModel()

            CreateFlashcardScreen(
                cadernoId = idCadernoSelecionado,
                arquivoId = idArquivoSelecionado,
                viewModel = perguntaAbertaViewModel,
                onNavigateBack = {
                    telaAtual = rotaRetornoAoCancelarCriacaoConteudo
                },
            )
        }

        RotasTelas.CRIAR_MULTIPLA_ESCOLHA -> {
            val multiplaEscolhaViewModel: CreateMultipleChoiceViewModel =
                viewModel()

            CreateMultipleChoiceScreen(
                cadernoId = idCadernoSelecionado,
                arquivoId = idArquivoSelecionado,
                viewModel = multiplaEscolhaViewModel,
                onNavigateBack = {
                    telaAtual = rotaRetornoAoCancelarCriacaoConteudo
                },
            )
        }

        RotasTelas.REVISAR_ARQUIVO -> {
            val revisarArquivoViewModel: RevisarArquivoViewModel =
                viewModel()

            RevisarArquivoScreen(
                cadernoId = idCadernoSelecionado,
                cadernoTitulo = nomeCadernoSelecionado,
                arquivoId = idArquivoSelecionado,
                tituloArquivo = tituloArquivoSelecionado,
                descricaoArquivo = descricaoArquivoSelecionado,
                viewModel = revisarArquivoViewModel,
                onFecharClick = {
                    telaAtual = RotasTelas.DETALHE_CADERNO
                },
                onAdicionarPerguntaAbertaClick = {
                    rotaRetornoAoCancelarCriacaoConteudo =
                        RotasTelas.REVISAR_ARQUIVO

                    telaAtual = RotasTelas.CRIAR_PERGUNTA_ABERTA
                },
                onAdicionarMultiplaEscolhaClick = {
                    rotaRetornoAoCancelarCriacaoConteudo =
                        RotasTelas.REVISAR_ARQUIVO

                    telaAtual = RotasTelas.CRIAR_MULTIPLA_ESCOLHA
                },
            )
        }
    }
}
