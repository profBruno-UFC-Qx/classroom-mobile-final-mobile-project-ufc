package com.memobrain.memonow.features.cadernos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreateMultipleChoiceScreen(
    viewModel: CreateMultipleChoiceViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4A5568)), // Fundo escuro
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .heightIn(max = 700.dp), // Evita que o card ocupe a tela inteira em telas grandes
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()) // Permite rolagem pelas opções
            ) {
                Text("Criar Múltipla Escolha", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Text("Cadastre a pergunta e marque a alternativa correta.", fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(24.dp))

                // Campo da Pergunta
                Text("PERGUNTA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                OutlinedTextField(
                    value = state.questionText,
                    onValueChange = { viewModel.onEvent(CreateMultipleChoiceEvent.OnQuestionChanged(it)) },
                    placeholder = { Text("Ex: Qual algoritmo utiliza hiperplanos...") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text("ALTERNATIVAS (Marque a correta)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))

                // Lista de 4 opções geradas dinamicamente
                state.options.forEachIndexed { index, optionText ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        RadioButton(
                            selected = state.correctOptionIndex == index,
                            onClick = { viewModel.onEvent(CreateMultipleChoiceEvent.OnCorrectOptionSelected(index)) },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2A5222)) // Verde para a correta
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = optionText,
                            onValueChange = { viewModel.onEvent(CreateMultipleChoiceEvent.OnOptionChanged(index, it)) },
                            placeholder = { Text("Opção ${index + 1}") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botões de Ação
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Cancelar", color = Color.DarkGray)
                    }
                    Button(
                        onClick = { 
                            viewModel.onEvent(CreateMultipleChoiceEvent.OnSaveClicked)
                            onNavigateBack()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isValid) Color(0xFF1E3A5F) else Color(0xFFBDBDBD)
                        ),
                        shape = RoundedCornerShape(50),
                        enabled = state.isValid
                    ) {
                        Text("Salvar", color = Color.White)
                    }
                }
            }
        }
    }
}
