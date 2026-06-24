package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FlashcardSummaryState(
    val questionNumber: String = "Q0",
    val progressPercentage: Float = 1.0f,
    val accuracyPercentage: String = "0%",
    val timeSpent: String = "0:00",
    val xpEarned: Int = 0
)

sealed class FlashcardSummaryEvent {
    object OnCloseClicked : FlashcardSummaryEvent()
}

class FlashcardSummaryViewModel : ViewModel() {
    private val _state = MutableStateFlow(FlashcardSummaryState())
    val state: StateFlow<FlashcardSummaryState> = _state.asStateFlow()

    private var _onNavigateBack: (() -> Unit)? = null

    fun setup(
        accuracy: String,
        time: String,
        xp: Int,
        totalQuestions: Int,
        onNavigateBack: () -> Unit
    ) {
        _onNavigateBack = onNavigateBack
        _state.value = FlashcardSummaryState(
            questionNumber = "Q$totalQuestions",
            progressPercentage = 1.0f,
            accuracyPercentage = accuracy,
            timeSpent = time,
            xpEarned = xp
        )
    }

    fun onEvent(event: FlashcardSummaryEvent) {
        when (event) {
            is FlashcardSummaryEvent.OnCloseClicked -> {
                _onNavigateBack?.invoke()
            }
        }
    }
}
