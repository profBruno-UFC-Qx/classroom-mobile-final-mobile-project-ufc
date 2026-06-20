package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// 🟢 Modelo de Dados da Questão
data class Questao(
    val id: String,
    val texto: String,
    val opcoes: List<String>,
    val indiceCorreto: Int
)

// 🟢 Estado da Tela
data class QuizUiState(
    val questoes: List<Questao> = emptyList(),
    val questaoAtualIndex: Int = 0,
    val opcaoSelecionada: Int? = null,
    val jaRespondeu: Boolean = false,
    val isRespostaCorreta: Boolean = false,
    val isLoading: Boolean = true,
    val erroMensagem: String? = null
) {
    val questaoAtual: Questao?
        get() = questoes.getOrNull(questaoAtualIndex)

    val progresso: Float
        get() = if (questoes.isEmpty()) 0f else (questaoAtualIndex + 1).toFloat() / questoes.size

    val numeroQuestaoDisplay: String
        get() = "Q${questaoAtualIndex + 1}"

    val porcentagemDisplay: String
        get() = "${(progresso * 100).toInt()}%"
}

class QuizViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        // 💡 Inicialização provisória. No futuro, a tela do Quiz vai
        // receber o ID real da navegação e chamar essa função diretamente.
        carregarQuestoesDoArquivo(arquivoId = "id_mock_teste")
    }

    // 🟢 FUNÇÃO PREPARADA PARA O FIREBASE
    fun carregarQuestoesDoArquivo(arquivoId: String) {

        // TODO (Para o colega do Firebase):
        // 1. Apague a lista de mockQuestoes abaixo.
        // 2. Use o parâmetro 'arquivoId' para fazer a query no Firestore (ex: db.collection("arquivos").document(arquivoId).collection("questoes")).
        // 3. Mapeie os dados recebidos para a nossa data class 'Questao'.
        // 4. Salve no estado usando: _uiState.update { it.copy(questoes = listaDoFirebase) }

        // --- INÍCIO DOS DADOS FALSOS (Apagar depois) ---
        val mockQuestoes = listOf(
            Questao(
                id = "1",
                texto = "Em Machine Learning, qual algoritmo utiliza hiperplanos para separar classes e busca maximizar a margem entre elas?",
                opcoes = listOf(
                    "K-Nearest Neighbors (KNN)",
                    "Support Vector Machine (SVM)",
                    "Decision Tree",
                    "Gradient Descent"
                ),
                indiceCorreto = 1 // SVM
            ),
            Questao(
                id = "2",
                texto = "Qual problema é mais associado ao algoritmo Machine Learning conhecido como Decision Tree quando a árvore cresce excessivamente?",
                opcoes = listOf(
                    "Overfitting",
                    "Underfitting",
                    "Normalização",
                    "Regularização"
                ),
                indiceCorreto = 0 // Overfitting
            )
        )
        // --- FIM DOS DADOS FALSOS ---

        _uiState.update { it.copy(questoes = mockQuestoes) }
    }

    fun selecionarOpcao(index: Int) {
        if (!_uiState.value.jaRespondeu) {
            _uiState.update { it.copy(opcaoSelecionada = index) }
        }
    }

    fun confirmarResposta() {
        val state = _uiState.value
        val questao = state.questaoAtual ?: return
        val selecionada = state.opcaoSelecionada ?: return

        val acertou = selecionada == questao.indiceCorreto

        _uiState.update {
            it.copy(
                jaRespondeu = true,
                isRespostaCorreta = acertou
            )
        }
    }

    fun proximaQuestao() {
        val state = _uiState.value
        if (state.questaoAtualIndex < state.questoes.size - 1) {
            viewModelScope.launch {
                _uiState.update { it.copy(jaRespondeu = false) }

                delay(600)

                _uiState.update {
                    it.copy(
                        questaoAtualIndex = it.questaoAtualIndex + 1,
                        opcaoSelecionada = null
                    )
                }
            }
        }
    }

    fun resetarQuiz() {
        _uiState.update {
            it.copy(
                questaoAtualIndex = 0,
                opcaoSelecionada = null,
                jaRespondeu = false
            )
        }
    }
}