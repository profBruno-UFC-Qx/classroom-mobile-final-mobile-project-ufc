package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import com.memobrain.memonow.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Criamos uma classe de dados simples para armazenar o título e a imagem do método
data class MetodoEstudoItem(
    val titulo: String,
    val iconeResId: Int
)

data class CriarArquivoUiState(
    val titulo: String = "",
    val descricao: String = "",
    val metodoSelecionado: String? = null,
    val mostrarBottomSheet: Boolean = false,
    val isSalvoSucesso: Boolean = false
)

class CriarArquivoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CriarArquivoUiState())
    val uiState: StateFlow<CriarArquivoUiState> = _uiState.asStateFlow()

    // 🟢 Lista atualizada passando o título e os ícones provisórios
    val listaMetodos = listOf(
        MetodoEstudoItem("Múltipla Escolha", R.drawable.ic_resposta_aberta),
        MetodoEstudoItem("Flashcard", R.drawable.ic_oclusao_imagem)
    )

    fun onTituloAlterado(novoTitulo: String) {
        _uiState.update { it.copy(titulo = novoTitulo) }
    }

    fun onDescricaoAlterada(novaDescricao: String) {
        _uiState.update { it.copy(descricao = novaDescricao) }
    }

    fun setMostrarBottomSheet(mostrar: Boolean) {
        _uiState.update { it.copy(mostrarBottomSheet = mostrar) }
    }

    fun selecionarMetodo(metodo: String) {
        _uiState.update { it.copy(metodoSelecionado = metodo, mostrarBottomSheet = false) }
    }

    fun salvarArquivo() {
        if (_uiState.value.titulo.isNotBlank() && _uiState.value.metodoSelecionado != null) {
            // 💡 Espaço reservado para o salvamento no Firebase Firestore vinculado ao ID do caderno
            _uiState.update { it.copy(isSalvoSucesso = true) }
        }
    }

    fun resetarEstado() {
        _uiState.value = CriarArquivoUiState()
    }
}