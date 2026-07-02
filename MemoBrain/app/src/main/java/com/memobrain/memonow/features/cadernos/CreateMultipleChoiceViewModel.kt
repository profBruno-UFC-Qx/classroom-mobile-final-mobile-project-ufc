package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import com.memobrain.memonow.data.repository.repositorio.RepositorioConteudo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OpcaoUi(
    val id: Long,
    val texto: String = "",
)

data class CreateMultipleChoiceUiState(
    val pergunta: String = "",
    val opcoes: List<OpcaoUi> =
        listOf(
            OpcaoUi(id = 1),
            OpcaoUi(id = 2),
            OpcaoUi(id = 3),
            OpcaoUi(id = 4),
        ),
    val indiceCorreto: Int? = null,
    val isSalvando: Boolean = false,
    val salvoComSucesso: Boolean = false,
    val mensagemErro: String? = null,
) {
    val podeAdicionarOpcao: Boolean
        get() = opcoes.size < 7

    val podeRemoverOpcao: Boolean
        get() = opcoes.size > 2
}

class CreateMultipleChoiceViewModel : ViewModel() {
    private val repositorioConteudo = RepositorioConteudo()

    private val _uiState = MutableStateFlow(CreateMultipleChoiceUiState())
    val uiState: StateFlow<CreateMultipleChoiceUiState> = _uiState.asStateFlow()

    fun onPerguntaChange(valor: String) {
        _uiState.update {
            it.copy(
                pergunta = valor,
                mensagemErro = null,
            )
        }
    }

    fun onOpcaoChange(
        indice: Int,
        valor: String,
    ) {
        _uiState.update { state ->
            val novaLista = state.opcoes.toMutableList()
            novaLista[indice] = novaLista[indice].copy(texto = valor)
            state.copy(
                opcoes = novaLista,
                mensagemErro = null,
            )
        }
    }

    fun selecionarCorreta(indice: Int) {
        _uiState.update {
            it.copy(
                indiceCorreto = indice,
                mensagemErro = null,
            )
        }
    }

    fun adicionarOpcao() {
        _uiState.update { state ->
            if (state.opcoes.size >= 7) return@update state

            val proximoId = (state.opcoes.maxOfOrNull { it.id } ?: 0L) + 1L
            state.copy(
                opcoes = state.opcoes + OpcaoUi(id = proximoId),
            )
        }
    }

    fun removerOpcao(indice: Int) {
        _uiState.update { state ->
            if (state.opcoes.size <= 2) return@update state

            val novaLista = state.opcoes.toMutableList()
            novaLista.removeAt(indice)

            val novoIndiceCorreto =
                when {
                    state.indiceCorreto == null -> null
                    state.indiceCorreto == indice -> null
                    state.indiceCorreto > indice -> state.indiceCorreto - 1
                    else -> state.indiceCorreto
                }

            state.copy(
                opcoes = novaLista,
                indiceCorreto = novoIndiceCorreto,
                mensagemErro = null,
            )
        }
    }

    fun salvarQuestao(
        cadernoId: String,
        arquivoId: String,
    ) {
        val state = _uiState.value

        if (state.isSalvando) return

        val pergunta = state.pergunta.trim()
        if (pergunta.isBlank()) {
            _uiState.update {
                it.copy(mensagemErro = "Digite a pergunta.")
            }
            return
        }

        val opcoesPreenchidas =
            state.opcoes
                .mapIndexedNotNull { indice, opcao ->
                    if (opcao.texto.trim().isNotBlank()) {
                        indice to opcao.texto.trim()
                    } else {
                        null
                    }
                }

        if (opcoesPreenchidas.size < 2) {
            _uiState.update {
                it.copy(mensagemErro = "Preencha pelo menos 2 alternativas.")
            }
            return
        }

        val indiceCorretoOriginal = state.indiceCorreto
        if (indiceCorretoOriginal == null) {
            _uiState.update {
                it.copy(mensagemErro = "Selecione a alternativa correta.")
            }
            return
        }

        val novaListaAlternativas = opcoesPreenchidas.map { it.second }
        val novoIndiceCorreto = opcoesPreenchidas.indexOfFirst { it.first == indiceCorretoOriginal }

        if (novoIndiceCorreto < 0) {
            _uiState.update {
                it.copy(mensagemErro = "A alternativa correta precisa estar preenchida.")
            }
            return
        }

        _uiState.update {
            it.copy(
                isSalvando = true,
                mensagemErro = null,
            )
        }

        repositorioConteudo.criarMultiplaEscolha(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
            pergunta = pergunta,
            alternativas = novaListaAlternativas,
            indiceCorreto = novoIndiceCorreto,
            aoSucesso = {
                _uiState.update {
                    it.copy(
                        isSalvando = false,
                        salvoComSucesso = true,
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

    fun consumirSucesso() {
        _uiState.update {
            it.copy(salvoComSucesso = false)
        }
    }
}
