package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import com.memobrain.memonow.data.repository.repositorio.RepositorioConteudo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CreateFlashcardState(
    val questionText: String = "",
    val answerText: String = "",
    val isSaving: Boolean = false,
    val salvoComSucesso: Boolean = false,
    val mensagemErro: String? = null,
) {
    val isValido: Boolean
        get() = questionText.isNotBlank() && answerText.isNotBlank()
}

class CreateFlashcardViewModel : ViewModel() {
    private val repositorioConteudo = RepositorioConteudo()

    private val _state = MutableStateFlow(CreateFlashcardState())
    val state: StateFlow<CreateFlashcardState> = _state.asStateFlow()

    fun onPerguntaAlterada(texto: String) {
        _state.update {
            it.copy(
                questionText = texto,
                mensagemErro = null,
            )
        }
    }

    fun onRespostaAlterada(texto: String) {
        _state.update {
            it.copy(
                answerText = texto,
                mensagemErro = null,
            )
        }
    }

    fun salvarFlashcard(
        cadernoId: String,
        arquivoId: String,
    ) {
        val estadoAtual = _state.value

        if (!estadoAtual.isValido || estadoAtual.isSaving) return

        _state.update {
            it.copy(
                isSaving = true,
                mensagemErro = null,
            )
        }

        repositorioConteudo.criarFlashcard(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
            pergunta = estadoAtual.questionText,
            resposta = estadoAtual.answerText,
            aoSucesso = {
                _state.update {
                    it.copy(
                        isSaving = false,
                        salvoComSucesso = true,
                    )
                }
            },
            aoErro = { erro ->
                _state.update {
                    it.copy(
                        isSaving = false,
                        mensagemErro = erro,
                    )
                }
            },
        )
    }

    fun consumirSucesso() {
        _state.update {
            it.copy(salvoComSucesso = false)
        }
    }

    fun limparFormulario() {
        _state.value = CreateFlashcardState()
    }
}
