package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FlashcardSummaryState(
    val questionNumber: String = "Q0",
    val progressPercentage: Float = 1f,
    val accuracyPercentage: String = "0%",
    val timeSpent: String = "0:00",
)

sealed class FlashcardSummaryEvent {
    object OnCloseClicked : FlashcardSummaryEvent()
}

class FlashcardSummaryViewModel : ViewModel() {
    private val _state =
        MutableStateFlow(
            FlashcardSummaryState(),
        )

    val state: StateFlow<FlashcardSummaryState> =
        _state.asStateFlow()

    private var onNavigateBack: (() -> Unit)? = null

    /*
     * Compatível com a navegação que já envia
     * percentual e tempo formatados.
     */
    fun setup(
        accuracy: String,
        time: String,
        xp: Int,
        totalQuestions: Int,
        onNavigateBack: () -> Unit,
    ) {
        this.onNavigateBack = onNavigateBack

        _state.value =
            FlashcardSummaryState(
                questionNumber = "Q$totalQuestions",
                progressPercentage = 1f,
                accuracyPercentage = accuracy,
                timeSpent = time,
            )
    }

    /*
     * Compatível com a navegação que envia
     * acertos e duração em milissegundos.
     */
    fun setup(
        correctAnswers: Int,
        totalQuestions: Int,
        durationMillis: Long,
        onNavigateBack: () -> Unit,
    ) {
        this.onNavigateBack = onNavigateBack

        val percentual =
            if (totalQuestions > 0) {
                (correctAnswers * 100) / totalQuestions
            } else {
                0
            }

        val segundosTotais = durationMillis / 1000
        val minutos = segundosTotais / 60
        val segundos = segundosTotais % 60

        _state.value =
            FlashcardSummaryState(
                questionNumber = "Q$totalQuestions",
                progressPercentage = 1f,
                accuracyPercentage = "$percentual%",
                timeSpent = "$minutos:${segundos.toString().padStart(2, '0')}",
            )
    }

    fun onEvent(event: FlashcardSummaryEvent) {
        when (event) {
            FlashcardSummaryEvent.OnCloseClicked -> {
                onNavigateBack?.invoke()
            }
        }
    }
}
