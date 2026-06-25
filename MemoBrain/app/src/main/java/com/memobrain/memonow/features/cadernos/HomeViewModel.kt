package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    private var listenerCadernos: ListenerRegistration? = null
    private var listenerHistorico: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(HomeUiState())

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        carregarDadosIniciais()
        carregarNomeUsuario()
        observarCadernos()
        observarHistorico()
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

    private fun carregarNomeUsuario() {
        viewModelScope.launch {
            val usuario =
                FirebaseAuth.getInstance().currentUser
                    ?: return@launch

            val documentoUsuario =
                runCatching {
                    FirebaseFirestore
                        .getInstance()
                        .collection("usuarios")
                        .document(usuario.uid)
                        .get()
                        .await()
                }.getOrNull()

            val nomeFirestore =
                documentoUsuario
                    ?.getString("nome")
                    .orEmpty()

            val nomeFinal =
                formatarNome(
                    nome =
                        nomeFirestore.ifBlank {
                            usuario.displayName.orEmpty()
                        },
                    email = usuario.email.orEmpty(),
                )

            _uiState.update {
                it.copy(nomeUsuario = nomeFinal)
            }
        }
    }

    private fun observarCadernos() {
        listenerCadernos?.remove()

        listenerCadernos =
            repositorioCaderno.observarCadernosDoUsuario(
                aoAtualizar = { cadernos ->
                    _uiState.update {
                        it.copy(
                            cadernosEmAndamento =
                                cadernos
                                    .take(5)
                                    .map { caderno ->
                                        CadernoAndamento(
                                            id = caderno.id,
                                            titulo = caderno.titulo,
                                        )
                                    },
                            isLoading = false,
                        )
                    }
                },
                aoErro = {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                },
            )
    }

    private fun observarHistorico() {
        listenerHistorico?.remove()

        listenerHistorico =
            repositorioHistorico.observarHistorico(
                aoAtualizar = { historicos ->
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
                    _uiState.update {
                        it.copy(isLoading = false)
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
