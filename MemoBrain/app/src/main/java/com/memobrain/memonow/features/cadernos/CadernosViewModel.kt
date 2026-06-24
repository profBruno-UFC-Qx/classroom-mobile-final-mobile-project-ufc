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
    val isLoading: Boolean = true,
    val mensagemErro: String? = null,
)

class CadernosViewModel : ViewModel() {
    private val repositorioCaderno = RepositorioCaderno()

    private var listenerCadernos: ListenerRegistration? = null

    private val _uiState = MutableStateFlow(CadernosUiState())
    val uiState: StateFlow<CadernosUiState> = _uiState.asStateFlow()

    init {
        carregarCadernos()
    }

    private fun carregarCadernos() {
        listenerCadernos?.remove()

        _uiState.update {
            it.copy(
                isLoading = true,
                mensagemErro = null,
            )
        }

        listenerCadernos =
            repositorioCaderno.observarCadernosDoUsuario(
                aoAtualizar = { cadernos ->
                    _uiState.update {
                        it.copy(
                            listaCadernos = cadernos,
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

    override fun onCleared() {
        listenerCadernos?.remove()
        super.onCleared()
    }
}
