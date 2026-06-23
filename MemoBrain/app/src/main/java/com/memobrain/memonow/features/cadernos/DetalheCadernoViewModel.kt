package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.memobrain.memonow.data.repository.repositorio.RepositorioArquivo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TopicoExercicio(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val metodo: String = "",
)

data class DetalheCadernoUiState(
    val nomeCaderno: String = "",
    val listaTopicos: List<TopicoExercicio> = emptyList(),
    val isLoading: Boolean = true,
    val mensagemErro: String? = null,
)

class DetalheCadernoViewModel : ViewModel() {
    private val repositorioArquivo = RepositorioArquivo()

    private var listenerArquivos: ListenerRegistration? = null
    private var cadernoAtualId: String? = null

    private val _uiState = MutableStateFlow(DetalheCadernoUiState())
    val uiState: StateFlow<DetalheCadernoUiState> = _uiState.asStateFlow()

    fun carregarArquivos(
        cadernoId: String,
        nomeCaderno: String,
    ) {
        if (cadernoId.isBlank()) {
            _uiState.value =
                DetalheCadernoUiState(
                    nomeCaderno = nomeCaderno,
                    isLoading = false,
                    mensagemErro = "Caderno não identificado.",
                )
            return
        }

        if (cadernoAtualId == cadernoId) {
            return
        }

        cadernoAtualId = cadernoId
        listenerArquivos?.remove()

        _uiState.value =
            DetalheCadernoUiState(
                nomeCaderno = nomeCaderno,
                isLoading = true,
            )

        listenerArquivos =
            repositorioArquivo.observarArquivosDoCaderno(
                cadernoId = cadernoId,
                aoAtualizar = { arquivos ->
                    val topicos =
                        arquivos.map { arquivo ->
                            TopicoExercicio(
                                id = arquivo.id,
                                titulo = arquivo.titulo,
                                descricao = arquivo.descricao,
                                metodo = arquivo.metodo,
                            )
                        }

                    _uiState.update {
                        it.copy(
                            listaTopicos = topicos,
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
        listenerArquivos?.remove()
        super.onCleared()
    }
}
