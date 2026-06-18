package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// O estado que a tela de listagem vai observar
data class CadernosUiState(
    val listaCadernos: List<Caderno> = emptyList(),
    val isLoading: Boolean = false
)

class CadernosViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CadernosUiState())
    val uiState: StateFlow<CadernosUiState> = _uiState.asStateFlow()

    init {
        carregarCadernos()
    }

    private fun carregarCadernos() {
        // Simulando a carga inicial. Seu amigo do Firebase vai conectar o Firestore aqui depois!
        val dadosSimulados = listOf(
            Caderno(id = "1", titulo = "Ciência de Dados", revisados = 6, restantes = 14),
            Caderno(id = "2", titulo = "Direito Constitucional", revisados = 7, restantes = 20),
            Caderno(id = "3", titulo = "Direito Processual Penal", revisados = 15, restantes = 48)
        )

        _uiState.update {
            it.copy(listaCadernos = dadosSimulados, isLoading = false)
        }
    }
}