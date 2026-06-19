package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// 🟢 CORREÇÃO: Adicionada a classe de dados que estava faltando para mapear os tópicos
data class TopicoExercicio(
    val titulo: String,
    val codigoPdf: String,
    val qtdQuestoes: Int
)

data class DetalheCadernoUiState(
    val nomeCaderno: String = "CIÊNCIA DE DADOS",
    val listaTopicos: List<TopicoExercicio> = emptyList(),
    val isLoading: Boolean = false
)

class DetalheCadernoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DetalheCadernoUiState())
    val uiState: StateFlow<DetalheCadernoUiState> = _uiState.asStateFlow()

    init {
        carregarTopicos()
    }

    private fun carregarTopicos() {
        _uiState.value = DetalheCadernoUiState(
            isLoading = false,
            listaTopicos = listOf(
                TopicoExercicio("Limpeza e Pré-processamento", "PDF 00", 14),
                TopicoExercicio("Algoritimos de Classificação", "PDF 01", 8),
                TopicoExercicio("Mineração de Dados", "PDF 02", 7),
                TopicoExercicio("OLAP e Data Warehouse", "PDF 03", 21)
            )
        )
    }
}