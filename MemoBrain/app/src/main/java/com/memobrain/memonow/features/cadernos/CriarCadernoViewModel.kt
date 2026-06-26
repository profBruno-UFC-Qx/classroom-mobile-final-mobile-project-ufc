package com.memobrain.memonow.features.cadernos

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.memobrain.memonow.data.repository.repositorio.RepositorioCaderno
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CriarCadernoUiState(
    val nome: String = "",
    val descricao: String = "",
    val corSelecionada: Long = 0xFF2D3748,
    val isSalvando: Boolean = false,
    val isSalvoSucesso: Boolean = false,
    val mensagemErro: String? = null,
)

class CriarCadernoViewModel : ViewModel() {
    private val repositorioCaderno = RepositorioCaderno()

    private val _uiState = MutableStateFlow(CriarCadernoUiState())
    val uiState: StateFlow<CriarCadernoUiState> = _uiState.asStateFlow()

    val listaCores =
        listOf(
            0xFF2C3E50,
            0xFF7B241C,
            0xFF6E6E2F,
            0xFF27AE60,
            0xFF16A085,
            0xFF1F286F,
            0xFF6C2C70,
            0xFFFF0000,
        )

    fun onNomeAlterado(novoNome: String) {
        _uiState.update {
            it.copy(
                nome = novoNome,
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

    fun onCorSelecionada(novaCor: Long) {
        _uiState.update {
            it.copy(
                corSelecionada = novaCor,
                mensagemErro = null,
            )
        }
    }

    fun salvarCaderno(imagemUri: Uri?) {
        val estadoAtual = _uiState.value

        if (estadoAtual.nome.isBlank() || estadoAtual.isSalvando) {
            return
        }

        _uiState.update {
            it.copy(
                isSalvando = true,
                mensagemErro = null,
            )
        }

        repositorioCaderno.criarCaderno(
            titulo = estadoAtual.nome,
            descricao = estadoAtual.descricao,
            cor = estadoAtual.corSelecionada,
            imagemUri = imagemUri,
            aoSucesso = { _ ->
                _uiState.update {
                    it.copy(
                        isSalvando = false,
                        isSalvoSucesso = true,
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

    fun resetarEstado() {
        _uiState.value = CriarCadernoUiState()
    }
}