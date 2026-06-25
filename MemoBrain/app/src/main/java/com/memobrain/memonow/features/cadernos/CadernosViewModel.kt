package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.memobrain.memonow.data.repository.repositorio.RepositorioCaderno
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CadernosUiState(
    val listaCadernos: List<Caderno> = emptyList(),
    val isLoading: Boolean = false,
    val mensagemErro: String? = null,
)

class CadernosViewModel : ViewModel() {
    private val repositorioCaderno = RepositorioCaderno()

    private var listenerCadernos: ListenerRegistration? = null
    private var usuarioIdAtual: String? = null

    private val _uiState = MutableStateFlow(CadernosUiState())

    val uiState: StateFlow<CadernosUiState> = _uiState.asStateFlow()

    fun carregarDadosDoUsuario(usuarioId: String?) {
        if (
            usuarioId == usuarioIdAtual &&
            listenerCadernos != null
        ) {
            return
        }

        usuarioIdAtual = usuarioId

        listenerCadernos?.remove()
        listenerCadernos = null

        _uiState.update {
            it.copy(
                listaCadernos = emptyList(),
                isLoading = !usuarioId.isNullOrBlank(),
                mensagemErro = null,
            )
        }

        if (usuarioId.isNullOrBlank()) {
            return
        }

        listenerCadernos =
            repositorioCaderno.observarCadernosDoUsuario(
                aoAtualizar = { cadernos ->
                    if (usuarioId != usuarioIdAtual) {
                        return@observarCadernosDoUsuario
                    }

                    _uiState.update {
                        it.copy(
                            listaCadernos = cadernos,
                            isLoading = false,
                            mensagemErro = null,
                        )
                    }
                },
                aoErro = { erro ->
                    if (usuarioId != usuarioIdAtual) {
                        return@observarCadernosDoUsuario
                    }

                    _uiState.update {
                        it.copy(
                            listaCadernos = emptyList(),
                            isLoading = false,
                            mensagemErro = erro,
                        )
                    }
                },
            )
    }

    override fun onCleared() {
        listenerCadernos?.remove()
        super.onCleared()
    }
}
