package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import com.memobrain.memonow.R
import com.memobrain.memonow.data.repository.repositorio.RepositorioArquivo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MetodoEstudoItem(
    val titulo: String,
    val iconeResId: Int,
)

data class ArquivoCriado(
    val id: String,
    val titulo: String,
    val descricao: String,
    val metodo: String,
)

data class CriarArquivoUiState(
    val titulo: String = "",
    val descricao: String = "",
    val metodoSelecionado: String? = null,
    val mostrarBottomSheet: Boolean = false,
    val isSalvando: Boolean = false,
    val arquivoCriado: ArquivoCriado? = null,
    val mensagemErro: String? = null,
)

class CriarArquivoViewModel : ViewModel() {
    private val repositorioArquivo = RepositorioArquivo()

    private val _uiState = MutableStateFlow(CriarArquivoUiState())
    val uiState: StateFlow<CriarArquivoUiState> = _uiState.asStateFlow()

    val listaMetodos =
        listOf(
            MetodoEstudoItem(
                titulo = "Múltipla Escolha",
                iconeResId = R.drawable.ic_resposta_aberta,
            ),
            MetodoEstudoItem(
                titulo = "Pergunta Aberta",
                iconeResId = R.drawable.ic_oclusao_imagem,
            ),
        )

    fun onTituloAlterado(novoTitulo: String) {
        _uiState.update {
            it.copy(
                titulo = novoTitulo,
                mensagemErro = null,
            )
        }
    }

    fun onDescricaoAlterada(novaDescricao: String) {
        _uiState.update {
            it.copy(
                descricao = novaDescricao,
                mensagemErro = null,
            )
        }
    }

    fun setMostrarBottomSheet(mostrar: Boolean) {
        _uiState.update {
            it.copy(mostrarBottomSheet = mostrar)
        }
    }

    fun selecionarMetodo(metodo: String) {
        _uiState.update {
            it.copy(
                metodoSelecionado = metodo,
                mostrarBottomSheet = false,
                mensagemErro = null,
            )
        }
    }

    fun salvarArquivo(cadernoId: String) {
        val estadoAtual = _uiState.value
        val metodo = estadoAtual.metodoSelecionado

        if (estadoAtual.isSalvando) return

        if (cadernoId.isBlank()) {
            _uiState.update {
                it.copy(
                    mensagemErro = "Não foi possível identificar o caderno.",
                )
            }
            return
        }

        if (estadoAtual.titulo.isBlank() || metodo == null) {
            _uiState.update {
                it.copy(
                    mensagemErro = "Preencha o título e selecione um método.",
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                isSalvando = true,
                mensagemErro = null,
            )
        }

        repositorioArquivo.criarArquivo(
            cadernoId = cadernoId,
            titulo = estadoAtual.titulo,
            descricao = estadoAtual.descricao,
            metodo = metodo,
            aoSucesso = { arquivoId ->
                _uiState.update {
                    it.copy(
                        isSalvando = false,
                        arquivoCriado =
                            ArquivoCriado(
                                id = arquivoId,
                                titulo = estadoAtual.titulo.trim(),
                                descricao = estadoAtual.descricao.trim(),
                                metodo = metodo,
                            ),
                    )
                }
            },
            aoErro = { erro ->
                _uiState.update {
                    it.copy(
                        isSalvando = false,
                        mensagemErro = erro,
                    )
                }
            },
        )
    }

    fun consumirArquivoCriado() {
        _uiState.update {
            it.copy(arquivoCriado = null)
        }
    }

    fun resetarEstado() {
        _uiState.value = CriarArquivoUiState()
    }
}
