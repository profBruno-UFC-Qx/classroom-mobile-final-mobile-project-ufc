package com.memobrain.memonow.features.cadernos

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.memobrain.memonow.data.repository.repositorio.RepositorioCaderno
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val COR_PADRAO_CADERNO = 0xFF264653L

data class EditNotebookState(
    val nome: String = "",
    val descricao: String = "",
    val corSelecionada: Long = COR_PADRAO_CADERNO,
    val coresDisponiveis: List<Long> =
        listOf(
            0xFF264653L,
            0xFF8A3324L,
            0xFF8B8000L,
            0xFF2A5222L,
            0xFF1B6B6BL,
            0xFF1E1E50L,
            0xFF5E2D79L,
            0xFF007BFFL,
        ),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isSavedSuccessfully: Boolean = false,
    val isDeletedSuccessfully: Boolean = false,
    val errorMessage: String? = null,
) {
    // Mantém compatibilidade com a tela antiga, caso ela ainda use estes nomes.
    val name: String
        get() = nome

    val description: String
        get() = descricao

    val selectedColor: Color?
        get() = Color(corSelecionada)

    val availableColors: List<Color>
        get() = coresDisponiveis.map { cor -> Color(cor) }
}

sealed class EditNotebookEvent {
    data class OnNameChanged(
        val name: String,
    ) : EditNotebookEvent()

    data class OnDescriptionChanged(
        val description: String,
    ) : EditNotebookEvent()

    object OnImageClicked : EditNotebookEvent()

    data class OnColorSelected(
        val color: Color,
    ) : EditNotebookEvent()

    object OnSaveClicked : EditNotebookEvent()

    object OnCancelClicked : EditNotebookEvent()
}

class EditNotebookViewModel : ViewModel() {
    private val repositorioCaderno = RepositorioCaderno()

    private var cadernoIdAtual: String = ""

    private val _state = MutableStateFlow(EditNotebookState())
    val state: StateFlow<EditNotebookState> = _state.asStateFlow()

    fun carregarCaderno(cadernoId: String) {
        if (cadernoId.isBlank()) {
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Não foi possível identificar o caderno.",
                )
            }
            return
        }

        cadernoIdAtual = cadernoId

        _state.update {
            it.copy(
                isLoading = true,
                isSavedSuccessfully = false,
                isDeletedSuccessfully = false,
                errorMessage = null,
            )
        }

        repositorioCaderno.buscarCaderno(
            cadernoId = cadernoId,
            aoSucesso = { caderno ->
                _state.update {
                    it.copy(
                        nome = caderno.titulo,
                        descricao = caderno.descricao,
                        corSelecionada = caderno.cor,
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            },
            aoErro = { erro ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = erro,
                    )
                }
            },
        )
    }

    fun onNomeAlterado(nome: String) {
        _state.update {
            it.copy(
                nome = nome,
                errorMessage = null,
            )
        }
    }

    fun onDescricaoAlterada(descricao: String) {
        _state.update {
            it.copy(
                descricao = descricao,
                errorMessage = null,
            )
        }
    }

    fun onCorSelecionada(cor: Long) {
        _state.update {
            it.copy(
                corSelecionada = cor,
                errorMessage = null,
            )
        }
    }

    fun salvarCaderno(cadernoId: String = cadernoIdAtual) {
        val estadoAtual = _state.value

        if (
            cadernoId.isBlank() ||
            estadoAtual.nome.isBlank() ||
            estadoAtual.isSaving ||
            estadoAtual.isDeleting
        ) {
            return
        }

        _state.update {
            it.copy(
                isSaving = true,
                errorMessage = null,
            )
        }

        repositorioCaderno.atualizarCaderno(
            cadernoId = cadernoId,
            titulo = estadoAtual.nome,
            descricao = estadoAtual.descricao,
            cor = estadoAtual.corSelecionada,
            aoSucesso = {
                _state.update {
                    it.copy(
                        isSaving = false,
                        isSavedSuccessfully = true,
                    )
                }
            },
            aoErro = { erro ->
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = erro,
                    )
                }
            },
        )
    }

    fun excluirCaderno(cadernoId: String = cadernoIdAtual) {
        if (
            cadernoId.isBlank() ||
            _state.value.isSaving ||
            _state.value.isDeleting
        ) {
            return
        }

        _state.update {
            it.copy(
                isDeleting = true,
                errorMessage = null,
            )
        }

        repositorioCaderno.excluirCaderno(
            cadernoId = cadernoId,
            aoSucesso = {
                _state.update {
                    it.copy(
                        isDeleting = false,
                        isDeletedSuccessfully = true,
                    )
                }
            },
            aoErro = { erro ->
                _state.update {
                    it.copy(
                        isDeleting = false,
                        errorMessage = erro,
                    )
                }
            },
        )
    }

    fun consumirResultado() {
        _state.update {
            it.copy(
                isSavedSuccessfully = false,
                isDeletedSuccessfully = false,
            )
        }
    }

    fun onEvent(event: EditNotebookEvent) {
        when (event) {
            is EditNotebookEvent.OnNameChanged -> {
                onNomeAlterado(event.name)
            }

            is EditNotebookEvent.OnDescriptionChanged -> {
                onDescricaoAlterada(event.description)
            }

            is EditNotebookEvent.OnColorSelected -> {
                onCorSelecionada(event.color.value.toLong())
            }

            EditNotebookEvent.OnSaveClicked -> {
                salvarCaderno()
            }

            EditNotebookEvent.OnImageClicked,
            EditNotebookEvent.OnCancelClicked,
            -> {
                Unit
            }
        }
    }
}
