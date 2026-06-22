package com.memobrain.memonow.features.cadernos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
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
fun CreateFlashcardScreen(
    viewModel: CreateFlashcardViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    // Fundo escurecido conforme a imagem
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF666666)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Criar Flashcard",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A2536)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Organize seu conhecimento com precisão e clareza.",
                    fontSize = 14.sp,
                    color = Color(0xFF7B8794),
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Campo PERGUNTA
                Text(
                    text = "PERGUNTA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8A94A6)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.questionText,
                    onValueChange = { viewModel.onEvent(CreateFlashcardEvent.OnQuestionChanged(it)) },
                    placeholder = { 
                        Text(
                            "Ex: O que é Machine Learning?", 
                            color = Color(0xFFB0B8C1),
                            fontSize = 14.sp
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFB0B8C1),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedBorderColor = Color(0xFF22496E)
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Campo RESPOSTA
                Text(
                    text = "RESPOSTA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8A94A6)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.answerText,
                    onValueChange = { viewModel.onEvent(CreateFlashcardEvent.OnAnswerChanged(it)) },
                    placeholder = { 
                        Text(
                            "Ex: Área da IA que permite aos sistemas aprenderem padrões....", 
                            color = Color(0xFFB0B8C1),
                            fontSize = 14.sp
                        ) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Box(modifier = Modifier.fillMaxHeight().padding(top = 16.dp), contentAlignment = Alignment.TopCenter) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = null,
                                tint = Color(0xFFB0B8C1),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedBorderColor = Color(0xFF22496E)
                    )
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Botões
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE9ECEF)),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Text("Cancelar", color = Color(0xFF495057), fontWeight = FontWeight.Bold)
                    }
                    
                    Button(
                        onClick = { 
                            viewModel.onEvent(CreateFlashcardEvent.OnSaveClicked)
                            onNavigateBack()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22496E)),
                        shape = RoundedCornerShape(26.dp)
                    ) {
                        Text("Salvar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
