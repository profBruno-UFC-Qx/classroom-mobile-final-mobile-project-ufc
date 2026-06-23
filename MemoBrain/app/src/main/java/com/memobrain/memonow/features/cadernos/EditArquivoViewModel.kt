package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import com.memobrain.memonow.data.repository.repositorio.RepositorioArquivo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EditArquivoState(
    val titulo: String = "",
    val descricao: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isSavedSuccessfully: Boolean = false,
    val isDeletedSuccessfully: Boolean = false,
    val errorMessage: String? = null,
)

class EditArquivoViewModel : ViewModel() {
    private val repositorioArquivo = RepositorioArquivo()

    private val _state = MutableStateFlow(EditArquivoState())
    val state: StateFlow<EditArquivoState> = _state.asStateFlow()

    fun carregarArquivo(
        cadernoId: String,
        arquivoId: String,
    ) {
        if (cadernoId.isBlank() || arquivoId.isBlank()) {
            _state.update {
                it.copy(
                    errorMessage = "Não foi possível identificar o arquivo.",
                )
            }
            return
        }

        _state.update {
            it.copy(
                isLoading = true,
                isSavedSuccessfully = false,
                isDeletedSuccessfully = false,
                errorMessage = null,
            )
        }

        repositorioArquivo.buscarArquivo(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
            aoSucesso = { arquivo ->
                _state.update {
                    it.copy(
                        titulo = arquivo.titulo,
                        descricao = arquivo.descricao,
                        isLoading = false,
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

    fun onTituloAlterado(titulo: String) {
        _state.update {
            it.copy(
                titulo = titulo,
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

    fun salvarArquivo(
        cadernoId: String,
        arquivoId: String,
    ) {
        val estadoAtual = _state.value

        if (
            estadoAtual.titulo.isBlank() ||
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

        repositorioArquivo.atualizarArquivo(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
            titulo = estadoAtual.titulo,
            descricao = estadoAtual.descricao,
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

    fun excluirArquivo(
        cadernoId: String,
        arquivoId: String,
    ) {
        if (_state.value.isSaving || _state.value.isDeleting) return

        _state.update {
            it.copy(
                isDeleting = true,
                errorMessage = null,
            )
        }

        repositorioArquivo.excluirArquivo(
            cadernoId = cadernoId,
            arquivoId = arquivoId,
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
}
