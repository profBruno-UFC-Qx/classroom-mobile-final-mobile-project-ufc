package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CreateMultipleChoiceState(
    val questionText: String = "",
    val options: List<String> = listOf("", "", "", ""), // 4 alternativas em branco
    val correctOptionIndex: Int? = null // Qual das 4 (0 a 3) é a verdadeira
) {
    // Validação simples para ativar o botão de salvar
    val isValid: Boolean get() =
        questionText.isNotBlank() &&
                options.all { it.isNotBlank() } &&
                correctOptionIndex != null
}

sealed class CreateMultipleChoiceEvent {
    data class OnQuestionChanged(val text: String) : CreateMultipleChoiceEvent()
    data class OnOptionChanged(val index: Int, val text: String) : CreateMultipleChoiceEvent()
    data class OnCorrectOptionSelected(val index: Int) : CreateMultipleChoiceEvent()
    object OnSaveClicked : CreateMultipleChoiceEvent()
    object OnCancelClicked : CreateMultipleChoiceEvent()
}

class CreateMultipleChoiceViewModel : ViewModel() {
    private val _state = MutableStateFlow(CreateMultipleChoiceState())
    val state: StateFlow<CreateMultipleChoiceState> = _state.asStateFlow()

    fun onEvent(event: CreateMultipleChoiceEvent) {
        when (event) {
            is CreateMultipleChoiceEvent.OnQuestionChanged -> {
                _state.update { it.copy(questionText = event.text) }
            }
            is CreateMultipleChoiceEvent.OnOptionChanged -> {
                _state.update { currentState ->
                    val newOptions = currentState.options.toMutableList()
                    newOptions[event.index] = event.text
                    currentState.copy(options = newOptions)
                }
            }
            is CreateMultipleChoiceEvent.OnCorrectOptionSelected -> {
                _state.update { it.copy(correctOptionIndex = event.index) }
            }
            is CreateMultipleChoiceEvent.OnSaveClicked -> {
                if (_state.value.isValid) {
                    println("Múltipla Escolha salva: ${_state.value}")
                    // Lógica para salvar no banco
                }
            }
            is CreateMultipleChoiceEvent.OnCancelClicked -> {
                // Fechar tela
            }
        }
    }
}
