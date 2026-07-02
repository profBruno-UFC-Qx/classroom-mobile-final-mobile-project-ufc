package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.memobrain.memonow.R
import com.memobrain.memonow.data.repository.repositorio.RepositorioCaderno
import com.memobrain.memonow.data.repository.repositorio.RepositorioHistorico
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

data class HomeUiState(
    val nomeUsuario: String = "Usuário",
    val chipsFiltros: List<String> = emptyList(),
    val chipSelecionado: String = "Todos",
    val metodosEstudo: List<MetodoEstudo> = emptyList(),
    val cadernosEmAndamento: List<CadernoAndamento> = emptyList(),
    val atividadesRecentes: List<AtividadeRecente> = emptyList(),
    val isLoading: Boolean = true,
)

class HomeViewModel : ViewModel() {
    private val repositorioCaderno = RepositorioCaderno()
    private val repositorioHistorico = RepositorioHistorico()

    private var listenerPerfil: ListenerRegistration? = null
    private var listenerCadernos: ListenerRegistration? = null
    private var listenerHistorico: ListenerRegistration? = null

    private var usuarioIdAtual: String? = null

    private val _uiState = MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        carregarDadosIniciais()
    }

    fun carregarDadosDoUsuario(usuarioId: String?) {
        if (usuarioId.isNullOrBlank()) {
            limparDadosDoUsuario()
            return
        }

        if (usuarioId == usuarioIdAtual) {
            return
        }

        usuarioIdAtual = usuarioId

        listenerPerfil?.remove()
        listenerCadernos?.remove()
        listenerHistorico?.remove()

        listenerPerfil = null
        listenerCadernos = null
        listenerHistorico = null

        _uiState.update {
            it.copy(
                nomeUsuario = "Usuário",
                cadernosEmAndamento = emptyList(),
                atividadesRecentes = emptyList(),
                isLoading = true,
            )
        }

        observarPerfil(usuarioId)
        observarCadernos(usuarioId)
        observarHistorico(usuarioId)
    }

    private fun limparDadosDoUsuario() {
        usuarioIdAtual = null

        listenerPerfil?.remove()
        listenerCadernos?.remove()
        listenerHistorico?.remove()

        listenerPerfil = null
        listenerCadernos = null
        listenerHistorico = null

        _uiState.update {
            it.copy(
                nomeUsuario = "Usuário",
                cadernosEmAndamento = emptyList(),
                atividadesRecentes = emptyList(),
                isLoading = false,
            )
        }
    }

    private fun carregarDadosIniciais() {
        val filtros =
            listOf(
                "Todos",
                "Revisar Hoje",
                "Em andamento",
                "Concluídos",
            )

        val metodos =
            listOf(
                MetodoEstudo(
                    titulo = "Múltipla Escolha",
                    iconeRes = R.drawable.ic_resposta_aberta,
                ),
                MetodoEstudo(
                    titulo = "Pergunta Aberta",
                    iconeRes = R.drawable.ic_oclusao_imagem,
                ),
            )

        _uiState.update {
            it.copy(
                chipsFiltros = filtros,
                metodosEstudo = metodos,
            )
        }
    }

    private fun observarPerfil(usuarioId: String) {
        listenerPerfil =
            FirebaseFirestore
                .getInstance()
                .collection("usuarios")
                .document(usuarioId)
                .addSnapshotListener { documento, _ ->
                    if (usuarioId != usuarioIdAtual) {
                        return@addSnapshotListener
                    }

                    val usuarioFirebase = FirebaseAuth.getInstance().currentUser

                    val nomeFirestore =
                        documento
                            ?.getString("nome")
                            .orEmpty()

                    val nomeFinal =
                        formatarNome(
                            nome =
                                nomeFirestore.ifBlank {
                                    usuarioFirebase?.displayName.orEmpty()
                                },
                            email = usuarioFirebase?.email.orEmpty(),
                        )

                    _uiState.update {
                        it.copy(nomeUsuario = nomeFinal)
                    }
                }
    }

    private fun observarCadernos(usuarioId: String) {
        listenerCadernos =
            repositorioCaderno.observarCadernosDoUsuario(
                aoAtualizar = { cadernos ->
                    if (usuarioId != usuarioIdAtual) {
                        return@observarCadernosDoUsuario
                    }

                    _uiState.update {
                        it.copy(
                            cadernosEmAndamento =
                                cadernos
                                    .take(5)
                                    .map { caderno ->
                                        CadernoAndamento(
                                            id = caderno.id,
                                            titulo = caderno.titulo,
                                            capaUrl = caderno.capaUrl
                                        )
                                    },
                            isLoading = false,
                        )
                    }
                },
                aoErro = {
                    if (usuarioId != usuarioIdAtual) {
                        return@observarCadernosDoUsuario
                    }

                    _uiState.update {
                        it.copy(
                            cadernosEmAndamento = emptyList(),
                            isLoading = false,
                        )
                    }
                },
            )
    }

    private fun observarHistorico(usuarioId: String) {
        listenerHistorico =
            repositorioHistorico.observarHistorico(
                aoAtualizar = { historicos ->
                    if (usuarioId != usuarioIdAtual) {
                        return@observarHistorico
                    }

                    val atividadesRecentes =
                        historicos
                            .take(5)
                            .map { historico ->
                                AtividadeRecente(
                                    idArquivo = historico.arquivoId,
                                    titulo = historico.arquivoTitulo,
                                    subtitulo = historico.cadernoTitulo,
                                    metodo = historico.metodo,
                                )
                            }

                    _uiState.update {
                        it.copy(
                            atividadesRecentes = atividadesRecentes,
                            isLoading = false,
                        )
                    }
                },
                aoErro = {
                    if (usuarioId != usuarioIdAtual) {
                        return@observarHistorico
                    }

                    _uiState.update {
                        it.copy(
                            atividadesRecentes = emptyList(),
                            isLoading = false,
                        )
                    }
                },
            )
    }

    fun selecionarFiltro(novoFiltro: String) {
        _uiState.update {
            it.copy(chipSelecionado = novoFiltro)
        }
    }

    override fun onCleared() {
        listenerPerfil?.remove()
        listenerCadernos?.remove()
        listenerHistorico?.remove()
        super.onCleared()
    }
}

private fun formatarNome(
    nome: String,
    email: String,
): String {
    val localePtBr = Locale.forLanguageTag("pt-BR")

    val base =
        nome
            .ifBlank {
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
