package com.memobrain.memonow.navegacao

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
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
import com.memobrain.memonow.features.cadernos.FlashcardSummaryScreen
import com.memobrain.memonow.features.cadernos.FlashcardSummaryViewModel
import com.memobrain.memonow.features.cadernos.ListaCadernosTela
import com.memobrain.memonow.features.cadernos.RevisarArquivoScreen
import com.memobrain.memonow.features.cadernos.RevisarArquivoViewModel
import com.memobrain.memonow.features.login.LoginTela
import com.memobrain.memonow.features.login.TelaInicial
import com.memobrain.memonow.features.perfil.ConfigTela
import com.memobrain.memonow.features.registrar.RegistrarTela
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.compareTo
import kotlin.text.clear

@Composable
fun AppNavegacao() {
    val backStack = rememberNavBackStack(RotaTela.Inicial)

    fun navegarParaAbaPrincipal(destino: RotaTela) {
        val jaEstaNoDestino =
            backStack.size == 1 &&
                backStack.lastOrNull() == destino

        if (jaEstaNoDestino) {
            return
        }

        backStack.clear()
        backStack.add(destino)
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

    val escopo = rememberCoroutineScope()

    var verificandoSessao by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {
        val usuarioFirebase = servicoLogin.obterUsuarioAtual()
        val sessao = armazenamentoSessao.sessaoFlow.first()

        val sessaoValida =
            usuarioFirebase != null &&
                sessao != null &&
                sessao.uid == usuarioFirebase.uid

        val rotaInicial =
            if (sessaoValida) {
                try {
                    usuarioFirebase.reload().await()
                    RotaTela.InicioApp
                } catch (exception: Exception) {
                    armazenamentoSessao.limparSessao()
                    RotaTela.Inicial
                }
            } else {
                armazenamentoSessao.limparSessao()
                RotaTela.Inicial
            }

        backStack.clear()
        backStack.add(rotaInicial)

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

    NavDisplay(
        backStack = backStack,
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
            ),
        onBack = {
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            }
        },
        entryProvider =
            entryProvider {
                entry<RotaTela.Inicial> {
                    TelaInicial(
                        entrar = {
                            backStack.add(RotaTela.Login)
                        },
                        registrar = {
                            backStack.add(RotaTela.Registrar)
                        },
                    )
                }

                entry<RotaTela.Login> {
                    LoginTela(
                        registrar = {
                            backStack.add(RotaTela.Registrar)
                        },
                        onLoginSucesso = {
                            navegarParaAbaPrincipal(
                                RotaTela.InicioApp,
                            )
                        },
                    )
                }

                entry<RotaTela.Registrar> {
                    RegistrarTela(
                        onCadastroSucesso = {
                            backStack.clear()
                            backStack.add(RotaTela.Login)
                        },
                        onIrParaLogin = {
                            backStack.clear()
                            backStack.add(RotaTela.Login)
                        },
                    )
                }

                entry<RotaTela.InicioApp> {
                    DashboardCadernosTela(
                        onIrParaCadernos = {
                            navegarParaAbaPrincipal(
                                RotaTela.Cadernos,
                            )
                        },
                        onIrParaPerfil = {
                            navegarParaAbaPrincipal(
                                RotaTela.Perfil,
                            )
                        },
                    )
                }

                entry<RotaTela.Cadernos> {
                    ListaCadernosTela(
                        onIrParaInicio = {
                            navegarParaAbaPrincipal(
                                RotaTela.InicioApp,
                            )
                        },
                        onIrParaPerfil = {
                            navegarParaAbaPrincipal(
                                RotaTela.Perfil,
                            )
                        },
                        onCadernoClick = { caderno ->
                            backStack.add(
                                RotaTela.DetalheCaderno(
                                    cadernoId = caderno.id,
                                    nomeCaderno = caderno.titulo,
                                ),
                            )
                        },
                        onEditarClick = { cadernoId ->
                            backStack.add(
                                RotaTela.EditarCaderno(
                                    cadernoId = cadernoId,
                                ),
                            )
                        },
                        onNovoCadernoClick = {
                            backStack.add(RotaTela.CriarCaderno)
                        },
                    )
                }

                entry<RotaTela.Perfil> {
                    ConfigTela(
                        onIrParaInicio = {
                            navegarParaAbaPrincipal(
                                RotaTela.InicioApp,
                            )
                        },
                        onIrParaCadernos = {
                            navegarParaAbaPrincipal(
                                RotaTela.Cadernos,
                            )
                        },
                        onSair = {
                            servicoLogin.logout()

                            escopo.launch {
                                armazenamentoSessao.limparSessao()

                                backStack.clear()
                                backStack.add(RotaTela.Inicial)
                            }
                        },
                        onContaExcluida = {
                            escopo.launch {
                                armazenamentoSessao.limparSessao()

                                backStack.clear()
                                backStack.add(RotaTela.Inicial)
                            }
                        },
                    )
                }

                entry<RotaTela.DetalheCaderno> { rota ->
                    DetalheCadernoScreen(
                        cadernoId = rota.cadernoId,
                        nomeCaderno = rota.nomeCaderno,
                        onBackClick = {
                            backStack.removeLastOrNull()
                        },
                        onIrParaInicio = {
                            navegarParaAbaPrincipal(
                                RotaTela.InicioApp,
                            )
                        },
                        onIrParaCadernos = {
                            navegarParaAbaPrincipal(
                                RotaTela.Cadernos,
                            )
                        },
                        onNovoArquivoClick = {
                            backStack.add(
                                RotaTela.CriarArquivo(
                                    cadernoId = rota.cadernoId,
                                ),
                            )
                        },
                        onTopicoClick = { arquivo ->
                            backStack.add(
                                RotaTela.RevisarArquivo(
                                    cadernoId = rota.cadernoId,
                                    cadernoTitulo = rota.nomeCaderno,
                                    arquivoId = arquivo.id,
                                    tituloArquivo = arquivo.titulo,
                                    descricaoArquivo = arquivo.descricao,
                                ),
                            )
                        },
                        onEditarArquivoClick = { arquivoId ->
                            backStack.add(
                                RotaTela.EditarArquivo(
                                    cadernoId = rota.cadernoId,
                                    arquivoId = arquivoId,
                                ),
                            )
                        },
                    )
                }

                entry<RotaTela.EditarCaderno> { rota ->
                    val editViewModel: EditNotebookViewModel = viewModel()

                    EditNotebookScreen(
                        cadernoId = rota.cadernoId,
                        viewModel = editViewModel,
                        onNavigateBack = {
                            backStack.removeLastOrNull()
                        },
                    )
                }

                entry<RotaTela.CriarCaderno> {
                    CriarCadernoScreen(
                        onBackClick = {
                            backStack.removeLastOrNull()
                        },
                    )
                }

                entry<RotaTela.CriarArquivo> { rota ->
                    CriarArquivoScreen(
                        cadernoId = rota.cadernoId,
                        onBackClick = {
                            backStack.removeLastOrNull()
                        },
                        onArquivoCriado = { arquivo ->
                            backStack.removeLastOrNull()

                            val proximaTela =
                                if (arquivo.metodo == "Múltipla Escolha") {
                                    RotaTela.CriarMultiplaEscolha(
                                        cadernoId = rota.cadernoId,
                                        arquivoId = arquivo.id,
                                    )
                                } else {
                                    RotaTela.CriarPerguntaAberta(
                                        cadernoId = rota.cadernoId,
                                        arquivoId = arquivo.id,
                                    )
                                }

                            backStack.add(proximaTela)
                        },
                    )
                }

                entry<RotaTela.EditarArquivo> { rota ->
                    val editArquivoViewModel: EditArquivoViewModel = viewModel()

                    EditArquivoScreen(
                        cadernoId = rota.cadernoId,
                        arquivoId = rota.arquivoId,
                        viewModel = editArquivoViewModel,
                        onNavigateBack = {
                            backStack.removeLastOrNull()
                        },
                    )
                }

                entry<RotaTela.CriarPerguntaAberta> { rota ->
                    val perguntaAbertaViewModel: CreateFlashcardViewModel =
                        viewModel()

                    CreateFlashcardScreen(
                        cadernoId = rota.cadernoId,
                        arquivoId = rota.arquivoId,
                        viewModel = perguntaAbertaViewModel,
                        onNavigateBack = {
                            backStack.removeLastOrNull()
                        },
                    )
                }

                entry<RotaTela.CriarMultiplaEscolha> { rota ->
                    val multiplaEscolhaViewModel:
                        CreateMultipleChoiceViewModel = viewModel()

                    CreateMultipleChoiceScreen(
                        cadernoId = rota.cadernoId,
                        arquivoId = rota.arquivoId,
                        viewModel = multiplaEscolhaViewModel,
                        onNavigateBack = {
                            backStack.removeLastOrNull()
                        },
                    )
                }

                entry<RotaTela.RevisarArquivo> { rota ->
                    val revisarArquivoViewModel:
                        RevisarArquivoViewModel = viewModel()

                    RevisarArquivoScreen(
                        cadernoId = rota.cadernoId,
                        cadernoTitulo = rota.cadernoTitulo,
                        arquivoId = rota.arquivoId,
                        tituloArquivo = rota.tituloArquivo,
                        descricaoArquivo = rota.descricaoArquivo,
                        viewModel = revisarArquivoViewModel,
                        onFecharClick = {
                            backStack.removeLastOrNull()
                        },
                        onAdicionarPerguntaAbertaClick = {
                            backStack.add(
                                RotaTela.CriarPerguntaAberta(
                                    cadernoId = rota.cadernoId,
                                    arquivoId = rota.arquivoId,
                                ),
                            )
                        },
                        onAdicionarMultiplaEscolhaClick = {
                            backStack.add(
                                RotaTela.CriarMultiplaEscolha(
                                    cadernoId = rota.cadernoId,
                                    arquivoId = rota.arquivoId,
                                ),
                            )
                        },
                        onFinalizado = { acertos, total, tempo ->
                            backStack.add(
                                RotaTela.ResumoFlashcard(
                                    acertos = acertos,
                                    totalQuestoes = total,
                                    tempoMillis = tempo,
                                ),
                            )
                        },
                    )
                }

                entry<RotaTela.ResumoFlashcard> { rota ->
                    val summaryViewModel: FlashcardSummaryViewModel = viewModel()

                    LaunchedEffect(
                        rota.acertos,
                        rota.totalQuestoes,
                        rota.tempoMillis,
                    ) {
                        summaryViewModel.setup(
                            correctAnswers = rota.acertos,
                            totalQuestions = rota.totalQuestoes,
                            durationMillis = rota.tempoMillis,
                            onNavigateBack = {
                                backStack.removeLastOrNull()
                            },
                        )
                    }

                    FlashcardSummaryScreen(
                        viewModel = summaryViewModel,
                    )
                }
            },
    )
}
