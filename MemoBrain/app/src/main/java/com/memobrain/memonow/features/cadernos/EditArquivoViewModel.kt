package com.memobrain.memonow.features.cadernos

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// 1. O Estado da Tela
data class EditArquivoState(
    val titulo: String = "",
    val codigoPdf: String = "",
    val qtdQuestoes: Int = 0
)

// 2. Os Eventos (Ações do Usuário)
sealed class EditArquivoEvent {
    data class OnTituloChanged(val titulo: String) : EditArquivoEvent()
    data class OnCodigoPdfChanged(val codigo: String) : EditArquivoEvent()
    data class OnQtdQuestoesChanged(val qtd: Int) : EditArquivoEvent()
    object OnSaveClicked : EditArquivoEvent()
    object OnCancelClicked : EditArquivoEvent()
}

// 3. O ViewModel
class EditArquivoViewModel : ViewModel() {
    private val _state = MutableStateFlow(EditArquivoState())
    val state: StateFlow<EditArquivoState> = _state.asStateFlow()

    fun onEvent(event: EditArquivoEvent) {
        when (event) {
            is EditArquivoEvent.OnTituloChanged -> {
                _state.update { it.copy(titulo = event.titulo) }
            }
            is EditArquivoEvent.OnCodigoPdfChanged -> {
                _state.update { it.copy(codigoPdf = event.codigo) }
            }
            is EditArquivoEvent.OnQtdQuestoesChanged -> {
                _state.update { it.copy(qtdQuestoes = event.qtd) }
            }
            is EditArquivoEvent.OnSaveClicked -> {
                // Lógica para salvar no banco futuramente
            }
            is EditArquivoEvent.OnCancelClicked -> {
                // Lógica para cancelar
            }
        }
    }
}
