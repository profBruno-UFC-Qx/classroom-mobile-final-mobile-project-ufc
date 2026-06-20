package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CriarCadernoUiState(
    val nome: String = "",
    val descricao: String = "",
    val corSelecionada: Long = 0xFF2D3748, // Cor padrão inicial
    val isSalvoSucesso: Boolean = false
)

class CriarCadernoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CriarCadernoUiState())
    val uiState: StateFlow<CriarCadernoUiState> = _uiState.asStateFlow()

    // Lista de cores baseada no Figma (image_1fb5b4.png)
    val listaCores = listOf(
        0xFF2C3E50, 0xFF7B241C, 0xFF6E6E2F, 0xFF27AE60,
        0xFF16A085, 0xFF1F286F, 0xFF6C2C70, 0xFFFF0000
    )

    fun onNomeAlterado(novoNome: String) {
        _uiState.update { it.copy(nome = novoNome) }
    }

    fun onDescricaoAlterada(novaDescricao: String) {
        _uiState.update { it.copy(descricao = novaDescricao) }
    }

    fun onCorSelecionada(novaCor: Long) {
        _uiState.update { it.copy(corSelecionada = novaCor) }
    }

    fun salvarCaderno() {
        val nomeCaderno = _uiState.value.nome
        val descricaoCaderno = _uiState.value.descricao
        val corCaderno = _uiState.value.corSelecionada

        if (nomeCaderno.isNotBlank()) {
            // 💡 AQUI depois vocês plugam o repositório do Firebase Firestore!
            // Exemplo: db.collection("cadernos").add(...)

            _uiState.update { it.copy(isSalvoSucesso = true) }
        }
    }

    fun resetarEstado() {
        _uiState.value = CriarCadernoUiState()
    }
}