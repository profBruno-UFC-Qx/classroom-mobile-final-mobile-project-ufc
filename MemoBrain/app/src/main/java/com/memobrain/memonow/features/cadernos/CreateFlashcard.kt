package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CreateFlashcardState(
    val questionText: String = "",
    val answerText: String = ""
)

sealed class CreateFlashcardEvent {
    data class OnQuestionChanged(val text: String) : CreateFlashcardEvent()
    data class OnAnswerChanged(val text: String) : CreateFlashcardEvent()
    object OnSaveClicked : CreateFlashcardEvent()
    object OnCancelClicked : CreateFlashcardEvent()
}

class CreateFlashcardViewModel : ViewModel() {
    private val _state = MutableStateFlow(CreateFlashcardState())
    val state: StateFlow<CreateFlashcardState> = _state.asStateFlow()

    fun onEvent(event: CreateFlashcardEvent) {
        when (event) {
            is CreateFlashcardEvent.OnQuestionChanged -> {
                _state.update { it.copy(questionText = event.text) }
            }
            is CreateFlashcardEvent.OnAnswerChanged -> {
                _state.update { it.copy(answerText = event.text) }
            }
            is CreateFlashcardEvent.OnSaveClicked -> {
                // Lógica para salvar no banco de dados
                println("Flashcard salvo: ${_state.value}")
            }
            is CreateFlashcardEvent.OnCancelClicked -> {
                // Lógica para fechar a tela
            }
        }
    }
}
