package com.memobrain.memonow.features.cadernos

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// 1. O Estado da Tela
data class EditNotebookState(
    val name: String = "",
    val description: String = "",
    val imageUri: String? = null,
    val selectedColor: Color? = null,
    val availableColors: List<Color> = listOf(
        Color(0xFF264653), Color(0xFF8A3324), Color(0xFF8B8000),
        Color(0xFF2A5222), Color(0xFF1B6B6B), Color(0xFF1E1E50),
        Color(0xFF5E2D79), Color(0xFF007BFF)
    )
)

// 2. Os Eventos (Ações do Usuário)
sealed class EditNotebookEvent {
    data class OnNameChanged(val name: String) : EditNotebookEvent()
    data class OnDescriptionChanged(val description: String) : EditNotebookEvent()
    object OnImageClicked : EditNotebookEvent()
    data class OnColorSelected(val color: Color) : EditNotebookEvent()
    object OnSaveClicked : EditNotebookEvent()
    object OnCancelClicked : EditNotebookEvent()
}

// 3. O ViewModel
class EditNotebookViewModel : ViewModel() {
    private val _state = MutableStateFlow(EditNotebookState())
    val state: StateFlow<EditNotebookState> = _state.asStateFlow()

    fun onEvent(event: EditNotebookEvent) {
        when (event) {
            is EditNotebookEvent.OnNameChanged -> {
                _state.update { it.copy(name = event.name) }
            }
            is EditNotebookEvent.OnDescriptionChanged -> {
                _state.update { it.copy(description = event.description) }
            }
            is EditNotebookEvent.OnImageClicked -> {
                // Aqui você acionaria a lógica para abrir a galeria
            }
            is EditNotebookEvent.OnColorSelected -> {
                _state.update { it.copy(selectedColor = event.color) }
            }
            is EditNotebookEvent.OnSaveClicked -> {
                // Lógica para salvar
            }
            is EditNotebookEvent.OnCancelClicked -> {
                // Lógica para descartar/fechar
            }
        }
    }
}
