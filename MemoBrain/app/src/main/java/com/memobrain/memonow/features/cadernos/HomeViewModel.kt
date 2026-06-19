package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import com.memobrain.memonow.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Estado da UI da tela Home (Telas 2)
data class HomeUiState(
    val nomeUsuario: String = "Allyson Novaes!",
    val chipsFiltros: List<String> = emptyList(),
    val chipSelecionado: String = "Todos",
    val metodosEstudo: List<MetodoEstudo> = emptyList(),
    val atividadesRecentes: List<AtividadeRecente> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        carregarDadosIniciais()
    }

    private fun carregarDadosIniciais() {
        // Dados simulados. Quando o Firebase estiver pronto, o Firestore vai popular isso aqui!
        val filtros = listOf("Todos", "Revisar Hoje", "Em andamento", "Concluidos")

        val metodos = listOf(
            MetodoEstudo("Oclusão de Imagem", R.drawable.ic_oclusao_imagem),
            MetodoEstudo("Resposta Aberta", R.drawable.ic_resposta_aberta)
        )

        val atividades = listOf(
            AtividadeRecente("Direito Administrativo", "Atos Administrativos"),
            AtividadeRecente("Português", "Morfologia"),
            AtividadeRecente("Ciência de Dados", "Mineração de Dados e Machine Learning")
        )

        _uiState.update {
            it.copy(
                chipsFiltros = filtros,
                metodosEstudo = metodos,
                atividadesRecentes = atividades,
                isLoading = false
            )
        }
    }

    fun selecionarFiltro(novoFiltro: String) {
        _uiState.update { it.copy(chipSelecionado = novoFiltro) }
    }
}