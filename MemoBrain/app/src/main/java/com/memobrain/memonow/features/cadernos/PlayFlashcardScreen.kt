package com.memobrain.memonow.features.cadernos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlayFlashcardScreen(
    viewModel: PlayFlashcardViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7FAFC))
            .padding(24.dp)
    ) {
        // Cabeçalho
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.Close, contentDescription = "Fechar")
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Machine Learning", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2A5222))
                Text("Resposta Aberta", fontSize = 14.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Barra de Progresso
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(state.questionNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            LinearProgressIndicator(
                progress = { state.progressPercentage },
                modifier = Modifier.weight(1f).height(8.dp).clip(CircleShape),
                color = Color(0xFF4DB6AC),
                trackColor = Color(0xFFE0F2F1)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("${(state.progressPercentage * 100).toInt()}%", fontSize = 14.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Conteúdo da Questão (Resposta Aberta)
        Column(modifier = Modifier.weight(1f)) {
            Text("PERGUNTA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.questionText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text("SUA RESPOSTA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.userAnswer,
                onValueChange = { viewModel.onEvent(PlayFlashcardEvent.OnUserAnswerChanged(it)) },
                placeholder = { Text("Digite sua resposta aqui...") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !state.isAnswerRevealed // Trava a digitação após revelar a resposta
            )

            // Revela a resposta esperada apenas quando o estado mudar
            if (state.isAnswerRevealed) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), // Fundo levemente verde
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("RESPOSTA CORRETA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2A5222))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.expectedAnswer,
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }

        // Rodapé
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { 
                    if (state.isAnswerRevealed) {
                        onNavigateBack()
                    } else {
                        viewModel.onEvent(PlayFlashcardEvent.OnCheckAnswer)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.userAnswer.isNotBlank()) Color(0xFF2A5222) else Color(0xFFBDBDBD)
                ),
                shape = RoundedCornerShape(25.dp),
                enabled = state.userAnswer.isNotBlank()
            ) {
                Text(if (state.isAnswerRevealed) "Finalizado" else "Checar >", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextButton(onClick = { }) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar", color = Color.Gray)
                }
                TextButton(onClick = { }) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Deletar", color = Color.Gray)
                }
                TextButton(onClick = { }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adicionar", color = Color.DarkGray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
