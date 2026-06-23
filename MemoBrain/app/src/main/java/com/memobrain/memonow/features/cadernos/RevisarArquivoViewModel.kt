package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import com.memobrain.memonow.data.remote.firestore.TipoConteudo
import com.memobrain.memonow.data.repository.repositorio.ConteudoEstudo
import com.memobrain.memonow.data.repository.repositorio.RepositorioConteudo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class RevisarArquivoUiState(
    val conteudos: List<ConteudoEstudo> = emptyList(),
    val indiceAtual: Int = 0,
    val respostaAberta: String = "",
    val opcaoSelecionada: Int? = null,
    val respondeu: Boolean = false,
    val respostaCorreta: Boolean = false,
    val feedbackVisivel: Boolean = false,
    val feedbackRespostaCorreta: String = "",
    val isLoading: Boolean = true,
    val mensagemErro: String? = null,
) {
    val conteudoAtual: ConteudoEstudo?
        get() = conteudos.getOrNull(indiceAtual)

    val ehPerguntaAberta: Boolean
        get() = conteudoAtual?.tipo == TipoConteudo.FLASHCARD

    val ehMultiplaEscolha: Boolean
        get() = conteudoAtual?.tipo == TipoConteudo.MULTIPLA_ESCOLHA

    val progresso: Float
        get() {
            if (conteudos.isEmpty()) return 0f
            return (indiceAtual + 1).toFloat() / conteudos.size
        }

    val numeroQuestao: String
        get() = "Q${indiceAtual + 1}"

    val porcentagem: String
        get() = "${(progresso * 100).toInt()}%"

    val temProximo: Boolean
        get() = indiceAtual < conteudos.lastIndex

    val podeConfirmar: Boolean
        get() =
            when {
                ehPerguntaAberta -> respostaAberta.isNotBlank()
                ehMultiplaEscolha -> opcaoSelecionada != null
                else -> false
            }
}

class RevisarArquivoViewModel : ViewModel() {
    private val repositorioConteudo = RepositorioConteudo()

    private var listenerConteudos: ListenerRegistration? = null

    private var cadernoIdAtual = ""
    private var arquivoIdAtual = ""

    private val _uiState =
        MutableStateFlow(
            RevisarArquivoUiState(),
        )

    val uiState: StateFlow<RevisarArquivoUiState> =
        _uiState.asStateFlow()

    fun carregarConteudos(
        cadernoId: String,
        cadernoTitulo: String,
        arquivoId: String,
        arquivoTitulo: String,
        arquivoDescricao: String,
    ) {
        cadernoIdAtual = cadernoId
        arquivoIdAtual = arquivoId

        listenerConteudos?.remove()

        _uiState.value =
            RevisarArquivoUiState(
                isLoading = true,
            )

        listenerConteudos =
            repositorioConteudo.observarConteudosDoArquivo(
                cadernoId = cadernoId,
                arquivoId = arquivoId,
                aoAtualizar = { conteudos ->
                    _uiState.update { estadoAnterior ->
                        val idAnterior = estadoAnterior.conteudoAtual?.id

                        val indiceNovo =
                            when {
                                conteudos.isEmpty() -> {
                                    0
                                }

                                idAnterior != null -> {
                                    val indiceMesmoConteudo =
                                        conteudos.indexOfFirst {
                                            it.id == idAnterior
                                        }

                                    if (indiceMesmoConteudo >= 0) {
                                        indiceMesmoConteudo
                                    } else {
                                        estadoAnterior.indiceAtual.coerceIn(
                                            0,
                                            conteudos.lastIndex,
                                        )
                                    }
                                }

                                else -> {
                                    estadoAnterior.indiceAtual.coerceIn(
                                        0,
                                        conteudos.lastIndex,
                                    )
                                }
                            }

                        val novoConteudoId =
                            conteudos.getOrNull(indiceNovo)?.id

                        val mudouConteudo =
                            idAnterior != novoConteudoId

                        estadoAnterior.copy(
                            conteudos = conteudos,
                            indiceAtual = indiceNovo,
                            respostaAberta =
                                if (mudouConteudo) {
                                    ""
                                } else {
                                    estadoAnterior.respostaAberta
                                },
                            opcaoSelecionada =
                                if (mudouConteudo) {
                                    null
                                } else {
                                    estadoAnterior.opcaoSelecionada
                                },
                            respondeu =
                                if (mudouConteudo) {
                                    false
                                } else {
                                    estadoAnterior.respondeu
                                },
                            respostaCorreta =
                                if (mudouConteudo) {
                                    false
                                } else {
                                    estadoAnterior.respostaCorreta
                                },
                            feedbackVisivel =
                                if (mudouConteudo) {
                                    false
                                } else {
                                    estadoAnterior.feedbackVisivel
                                },
                            feedbackRespostaCorreta =
                                if (mudouConteudo) {
                                    ""
                                } else {
                                    estadoAnterior.feedbackRespostaCorreta
                                },
                            isLoading = false,
                            mensagemErro = null,
                        )
                    }
                },
                aoErro = { erro ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            mensagemErro = erro,
                        )
                    }
                },
            )
    }

    fun atualizarRespostaAberta(texto: String) {
        if (_uiState.value.respondeu) return

        _uiState.update {
            it.copy(respostaAberta = texto)
        }
    }

    fun selecionarOpcao(indice: Int) {
        if (_uiState.value.respondeu) return

        _uiState.update {
            it.copy(opcaoSelecionada = indice)
        }
    }

    fun confirmarResposta() {
        val estadoAtual = _uiState.value
        val conteudo = estadoAtual.conteudoAtual ?: return

        if (estadoAtual.respondeu) return

        val acertou =
            when (conteudo.tipo) {
                TipoConteudo.FLASHCARD -> {
                    if (estadoAtual.respostaAberta.isBlank()) return

                    normalizarTexto(estadoAtual.respostaAberta) ==
                        normalizarTexto(conteudo.resposta)
                }

                TipoConteudo.MULTIPLA_ESCOLHA -> {
                    val indiceSelecionado =
                        estadoAtual.opcaoSelecionada ?: return

                    indiceSelecionado == conteudo.indiceCorreto
                }

                else -> {
                    return
                }
            }

        val respostaCorretaTexto =
            when (conteudo.tipo) {
                TipoConteudo.FLASHCARD -> {
                    conteudo.resposta
                }

                TipoConteudo.MULTIPLA_ESCOLHA -> {
                    conteudo.alternativas
                        .getOrNull(conteudo.indiceCorreto)
                        .orEmpty()
                }

                else -> {
                    ""
                }
            }

        _uiState.update {
            it.copy(
                respondeu = true,
                respostaCorreta = acertou,
                feedbackVisivel = true,
                feedbackRespostaCorreta = respostaCorretaTexto,
            )
        }
    }

    fun continuarParaProximo(onFinalizado: () -> Unit) {
        val estadoAtual = _uiState.value

        if (!estadoAtual.temProximo) {
            _uiState.update {
                it.copy(feedbackVisivel = false)
            }

            onFinalizado()
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(feedbackVisivel = false)
            }

            delay(180)

            _uiState.update {
                it.copy(
                    indiceAtual = it.indiceAtual + 1,
                    respostaAberta = "",
                    opcaoSelecionada = null,
                    respondeu = false,
                    respostaCorreta = false,
                    feedbackVisivel = false,
                    feedbackRespostaCorreta = "",
                    mensagemErro = null,
                )
            }
        }
    }

    fun excluirConteudoAtual() {
        val conteudoId = _uiState.value.conteudoAtual?.id ?: return

        repositorioConteudo.excluirConteudo(
            cadernoId = cadernoIdAtual,
            arquivoId = arquivoIdAtual,
            conteudoId = conteudoId,
            aoSucesso = {},
            aoErro = { erro ->
                _uiState.update {
                    it.copy(mensagemErro = erro)
                }
            },
        )
    }

    private fun normalizarTexto(texto: String): String =
        texto
            .trim()
            .lowercase(Locale.ROOT)
            .replace(
                Regex("\\s+"),
                " ",
            )

    override fun onCleared() {
        listenerConteudos?.remove()
        super.onCleared()
    }
}
