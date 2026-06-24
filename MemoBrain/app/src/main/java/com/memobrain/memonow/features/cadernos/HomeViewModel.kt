package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.memobrain.memonow.R
import com.memobrain.memonow.data.repository.repositorio.RepositorioHistorico
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val nomeUsuario: String = "Allyson Novaes!",
    val chipsFiltros: List<String> = emptyList(),
    val chipSelecionado: String = "Todos",
    val metodosEstudo: List<MetodoEstudo> = emptyList(),
    val cadernosEmAndamento: List<CadernoAndamento> = emptyList(),
    val atividadesRecentes: List<AtividadeRecente> = emptyList(),
    val isLoading: Boolean = true,
)

class HomeViewModel : ViewModel() {
    private val repositorioHistorico = RepositorioHistorico()

    private var listenerHistorico: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        carregarDadosIniciais()
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

                    val cadernosEmAndamento =
                        historicos
                            .distinctBy { historico ->
                                historico.cadernoId
                            }.take(5)
                            .map { historico ->
                                CadernoAndamento(
                                    id = historico.cadernoId,
                                    titulo = historico.cadernoTitulo,
                                )
                            }

                    _uiState.update {
                        it.copy(
                            cadernosEmAndamento = cadernosEmAndamento,
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
        listenerHistorico?.remove()
        super.onCleared()
    }
}
