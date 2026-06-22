package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PlayFlashcardState(
    val questionText: String = "O que é Machine Learning?",
    val expectedAnswer: String = "Área da IA que permite aos sistemas aprenderem padrões e tomarem decisões com base em dados.",
    val userAnswer: String = "",
    val isAnswerRevealed: Boolean = false
) {
    val progressPercentage: Float = 1.0f
    val questionNumber: String = "Q1"
}

sealed class PlayFlashcardEvent {
    data class OnUserAnswerChanged(val text: String) : PlayFlashcardEvent()
    object OnCheckAnswer : PlayFlashcardEvent()
    object OnCloseClicked : PlayFlashcardEvent()
}

class PlayFlashcardViewModel : ViewModel() {
    private val _state = MutableStateFlow(PlayFlashcardState())
    val state: StateFlow<PlayFlashcardState> = _state.asStateFlow()

    fun onEvent(event: PlayFlashcardEvent) {
        when (event) {
            is PlayFlashcardEvent.OnUserAnswerChanged -> {
                _state.update { it.copy(userAnswer = event.text) }
            }
            is PlayFlashcardEvent.OnCheckAnswer -> {
                _state.update { it.copy(isAnswerRevealed = true) }
            }
            is PlayFlashcardEvent.OnCloseClicked -> {
                // Lógica para fechar a tela de jogo
            }
        }
    }
}
